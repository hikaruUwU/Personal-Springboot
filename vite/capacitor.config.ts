import type {CapacitorConfig} from '@capacitor/cli';

const config: CapacitorConfig = {
    appId: 'project.sekai.base',
    appName: 'basement',
    webDir: 'dist',
    "server": {
        "androidScheme": "http"
    }
};

export default config;
