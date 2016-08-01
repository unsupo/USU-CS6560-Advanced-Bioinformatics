if [ "$1a" == "a" ]; then
   echo "must give a commit message"
   exit;
fi;
branch=master
if [ "$2a" != "a" ]; then
   branch=$2
fi
git add .
git commit -m "$1"
git push -f -u origin $branch
