#!/bin/sh
# run from top level dir
rm -rf target/docs
git clone git@github.com:atomix/trinity.git target/docs -b gh-pages
lein doc
cd target/docs
git add -A
git commit -m "Updated Docs"
git push origin gh-pages