# android-lint-checks
Checks for Android Lint

How to debug:
```
step 1: ./gradlew --no-daemon -Dorg.gradle.debug=true lintDebug
step 2: Attach to Process...
```

Check custom lint check jar file is working:
```
~/Library/Android/sdk/tools/bin/lint --show
```

Some gradle tasks:
```bash
# build jar
./gradlew :lint-checks:build

# run lint checks
./gradlew :app:lintVnRelease
```