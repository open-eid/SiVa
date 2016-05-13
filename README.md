# Signature Verification Service [![Build Status](https://travis-ci.org/open-eid/SiVa.svg?branch=develop)](https://travis-ci.org/open-eid/SiVa)

Used for validating digital signature container files.
This project depends on [Digital Signature Service (DSS)](https://github.com/esig/dss)

How to build
------------

### Using Maven Wrapper

Recommended way of building this project is using [Maven Wrapper](https://github.com/takari/maven-wrapper) to build it.
Run following command:

```bash
./mvnw clean package -Dmaven.test.skip.exec
```

Or even easier is just run:

```bash
./build.sh
```

### When You have Maven installed

```bash
mvn clean install -Dmaven.test.skip.exec -DargLine="-Xmx512m"
```

Above command will have quite long running time and may run into complications due to DSS tests, to skip these tests run:
```bash
 mvn clean install -Dmaven.test.skip=true
```

Note: Make sure that you have also pulled the DSS submodule, otherwise the build will fail.
To pull the DSS submodule run following commands:

```bash
git submodule init
git submodule update
```

