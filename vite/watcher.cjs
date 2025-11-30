// native-watch.js

const fs = require('fs');
const path = require('path');
const {exec} = require('child_process');

// --- 配置区域 ---
const BUILD_COMMAND = 'npm run build';
const WATCH_ROOT_DIRS = ['src', 'public']; // 需要监控的顶级目录
const WATCH_FILES = ['vite.config.js', 'vite.config.ts', 'index.html', 'package.json']; // 需要监控的根文件
const IGNORE_DIRS = ['node_modules', 'dist', '.git']; // 忽略的目录
const DEBOUNCE_TIME = 500; // 500毫秒防抖动时间
// -----------------

let timeoutId = null;
let isBuilding = false;

/**
 * 遍历目录并收集所有需要设置 fs.watch 的目录路径
 * @param {string} dir - 当前检查的目录
 * @param {Array<string>} watchDirs - 收集到的目录数组
 */
function collectWatchDirs(dir, watchDirs) {
    try {
        const files = fs.readdirSync(dir, {withFileTypes: true});

        files.forEach(file => {
            const fullPath = path.join(dir, file.name);

            if (IGNORE_DIRS.includes(file.name)) {
                return; // 忽略此目录
            }

            if (file.isDirectory()) {
                watchDirs.push(fullPath);
                collectWatchDirs(fullPath, watchDirs); // 递归
            }
        });
    } catch (err) {
        if (err.code !== 'ENOENT') console.error(`Error reading directory ${dir}:`, err);
    }
}

/**
 * 执行构建命令，并处理退出逻辑
 */
function runBuild(triggerPath) {
    if (isBuilding) {
        // console.log('[WATCH] Build is already running. Skipping trigger.');
        return;
    }

    isBuilding = true;
    console.log(`\n[WATCH] Change detected in: ${triggerPath}. Starting build...`);

    // 使用 exec 执行 shell 命令
    const buildProcess = exec(BUILD_COMMAND, (error, stdout, stderr) => {
        isBuilding = false;

        if (error) {
            console.error(`\n--- ❌ BUILD FAILED ❌ ---`);
            console.error(`Exit Code: ${error.code}. Shutting down watch service.`);
            console.error('--------------------------\n');

            // *** 核心逻辑：失败时强制退出进程 ***
            process.exit(error.code || 1);
        }

        console.log(`\n--- ✅ BUILD SUCCESS ✅ ---`);
        console.log(`Completed at ${new Date().toLocaleTimeString()}`);
        console.log('--------------------------\n');
    });

    // 实时流式传输输出
    buildProcess.stdout.pipe(process.stdout);
    buildProcess.stderr.pipe(process.stderr);
}

/**
 * 触发构建的防抖函数
 */
function triggerBuild(filePath) {
    // 清除上次的定时器
    if (timeoutId) {
        clearTimeout(timeoutId);
    }

    // 设置新的定时器
    timeoutId = setTimeout(() => {
        runBuild(filePath);
        timeoutId = null;
    }, DEBOUNCE_TIME);
}

// -------------------
// 启动监控服务
// -------------------

// 1. 收集所有需要监控的目录
const allWatchDirs = [];
WATCH_ROOT_DIRS.forEach(dir => {
    allWatchDirs.push(dir);
    collectWatchDirs(dir, allWatchDirs);
});

// 2. 为每个目录设置 fs.watch
allWatchDirs.forEach(dir => {
    if (!fs.existsSync(dir)) {
        console.warn(`Warning: Directory not found: ${dir}`);
        return;
    }
    // 注意：recursive: true在macOS和Windows上不可靠，我们通过collectWatchDirs手动实现递归
    fs.watch(dir, (eventType, filename) => {
        // 在某些系统上，eventType和filename可能不可用或不准确
        const fullPath = filename ? path.join(dir, filename) : dir;
        // 简单地使用防抖动来处理事件
        triggerBuild(fullPath);
    });
});

// 3. 为根文件设置 fs.watchFile (fs.watch 对根文件不友好，且 fs.watchFile更稳定)
WATCH_FILES.forEach(file => {
    if (fs.existsSync(file)) {
        // watchFile是轮询模式，相对稳定，但消耗更高。
        fs.watchFile(file, (curr, prev) => {
            if (curr.mtime.getTime() !== prev.mtime.getTime()) {
                triggerBuild(file);
            }
        });
    } else {
        console.warn(`Warning: Root file not found: ${file}`);
    }
});

console.log('-------------------------------------------');
console.log('Native Watch Service Ready. Waiting for changes...');
console.log(`Watching ${allWatchDirs.length} directories and ${WATCH_FILES.length} files.`);
console.log('-------------------------------------------\n');
runBuild('initial run');