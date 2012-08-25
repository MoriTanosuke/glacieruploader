glacieruploader
===============

A simple java command line application for Amazon Glacier

How to run
==========

You need a file names ´aws.properties´ in your classpath with 2 lines:

    accessKey=your_aws_access_key
    secretKey=your_secret_key

Upload
------

You'll call the tool like this:

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --file /path/to/my/file.zip
    
    Starting to upload README.md...
    Using endpoint https://glacier.eu-west-1.amazonaws.com
    Starting upload of file.zip
    Uploaded archive j7UL7pH46FJGhoAxNVDsdjhHs_GLSKGLd12Dq44dfiyTciW6DSCQubctUFEZ4nKWPrJzv_YoxPVK_TfdAuMCxiQIE3_zEGDg84luI0-tzWMusdfjKHG2ILuhJhK5PySOOaw

This will return an archive id which you can use to retrieve the archive again later.

List inventory
--------------

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --list-inventory

This will list the complete inventory of the vault and you can copy the archive ids that you want to retrieve.

Download archive
================

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --download myarchiveid

This will retrieve the archive.

This is not working for me. What should I do?
=============================================

Go to my [Issues page][1] and add a new issue with a nice report about what's failing for you.
Try to provide enough information so I can help you out and fix the error for everyone. It 
might be a good idea to check for existing issues before reporting a new one.

And as always, you can [fork me and fix the error yourself][2]. :-)



[0]: https://github.com/MoriTanosuke/glacieruploader/downloads
[1]: https://github.com/MoriTanosuke/glacieruploader/issues
[2]: https://github.com/MoriTanosuke/glacieruploader/fork
