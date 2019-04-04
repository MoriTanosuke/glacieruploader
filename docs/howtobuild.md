How to build
============

Open a command line in the directory of the `pom.xml` file and run the following command:

    ./mvwn clean package

OWASP dependency check
----------------------

If you want to run the OWASP dependency check while building, you can
activate the profile `owasp-check`:

    ./mvnw package -Powasp-check

This step requires internet connection to download the CVE databases and 
might fail if the download is not successful.
