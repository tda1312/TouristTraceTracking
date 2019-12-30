# Tourist Trace Tracking sub-repos:
#### Backend API: https://github.com/phuongminh2303/TouristTraceAPI
#### Web repo: https://github.com/praise2112/TouristTraceTrackingWeb

# Tourist Trace Tracking git guide:

## What you need to do ONE TIME:
- **Fork this repo**: so that you will have a "thing" to push code to and pull request to me or compare code in different branches.
- **Pull YOUR forked repo to your machine**: to have a local repo.
- In your local repo, check if you have 2 branches: **master and dev**. Then create another branch to work in (dev_<yourname>).
- Create **upstream** remote so that you can fetch new code **FROM OUR REPO**:
```sh
$ git remote add upstream https://github.com/phuongminh2303/TouristTraceTracking.git
```

## What you need to do BEFORE YOU ADD NEW THING:
- Fetch the latest code form our repo:
```sh
$ git checkout master  # I am not sure if we need this :))
$ git fetch upstream
$ git checkout dev
$ git merge upstream/dev # So now you successfully get the latest code 
```
- Then move to branch dev_<yourname> and merge the code. If you've already had some new code before, 99.69% you would have a conflict. Use the **stash** technique I teached you this noon!

## What you need to do AFTER YOU ADDED NEW THINGS:
- Commit your code in your dev_name branch.
- Checkout to dev branch and merge dev_name to it.
- Push to your forked repo on Github.
- Make a pull request, tell me to review.

## What you NEVER DO:
- **TOUCH MASTER BRANCH**
- git push --force
