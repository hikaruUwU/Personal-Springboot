mkdir instance

git --work-tree=./instance checkout -f

cp ./instance/post-receive ./hooks/

chmod u+x ./hooks/post-receive