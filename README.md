glacieruploader
===============

A simple java command line application for Amazon Glacier. You can find the latest version at [https://github.com/MoriTanosuke/glacieruploader/](https://github.com/MoriTanosuke/glacieruploader/).

[![Build Status](https://secure.travis-ci.org/MoriTanosuke/glacieruploader.png?branch=master)](http://travis-ci.org/MoriTanosuke/glacieruploader)

How to get it
=============

Either [download the latest binary](https://github.com/MoriTanosuke/glacieruploader/downloads), or [build from source](https://github.com/MoriTanosuke/glacieruploader/wiki/How-to-build).

How to run
==========

You need a file named `aws.properties` with 2 lines:

    accessKey=your_aws_access_key
    secretKey=your_secret_key

When running the uploader, specify the path to this file with `--credentials /path/to/aws.properties`. If
you don't specify this option, the default is to search for the file in javas `user.home` directory.

Create vault
------------

Available since 0.0.4.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --create

Delete vault
------------

Available since 0.0.4.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --delete

Upload archive
--------------

Available since 0.0.3.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --upload /path/to/my/file.zip
    
    Starting to upload file.zip...
    Using endpoint https://glacier.eu-west-1.amazonaws.com
    Starting upload of file.zip
    Uploaded archive j7UL7pH46FJGhoAxNVDsdjhHs_GLSKGLd12Dq44dfiyTciW6DSCQubctUFEZ4nKWPrJzv_YoxPVK_TfdAuMCxiQIE3_zEGDg84luI0-tzWMusdfjKHG2ILuhJhK5PySOOaw

This will return an archive id which you can use to retrieve the archive again later.

Download archive
----------------

Available since 0.0.4.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --download myarchiveid

This will download the archive to a temporary location and append the prefix `glacier-` and the postfix `.dl`
to the file. *This will change later and you'll provide the file location.*

List inventory
--------------

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --list-inventory
    Listing inventory for vault myvaultname...
    Inventory Job ID=8yM9rC4RvSKW5QlXdsglkjJHDFGPMSQyZA2CjhpIWgw2AE4lyyIU87uZz2d-b8eoKrCbGehR4vj5dfHiKPA9Zj5

This will give you a job ID for the inventory listing of the vault. After the job is completed, you can retrieve the listing.

Retrieve inventory listing
--------------------------

Available since 0.0.3.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --list-inventory yourjobidfromthepreviousstep
    Retrieving inventory for job id 8yM9rC4RvSKW5QlXdsglkjJHDFGPMSQyZA2CjhpIWgw2AE4lyyIU87uZz2d-b8eoKrCbGehR4vj5dfHiKPA9Zj5...
    Using endpoint https://glacier.eu-west-1.amazonaws.com

Download archive
----------------

Available since 0.0.3.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --download myarchiveid

This will retrieve the archive.

This is not working for me. What should I do?
=============================================

Go to my [Issues page][1] and add a new issue with a nice report about what's failing for you.
Try to provide enough information so I can help you out and fix the error for everyone. It 
might be a good idea to check for existing issues before reporting a new one.

And as always, you can [fork me and fix the error yourself][2]. :-)

License
=======

This project is distributed under [GNU GPL v3][3].


[0]: https://github.com/MoriTanosuke/glacieruploader/downloads
[1]: https://github.com/MoriTanosuke/glacieruploader/issues
[2]: https://github.com/MoriTanosuke/glacieruploader/fork
[3]: http://www.gnu.org/licenses/gpl-3.0.html
