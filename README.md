glacieruploader
===============

A simple java command line application for Amazon Glacier. You can find the latest version at [https://github.com/MoriTanosuke/glacieruploader/][6].

How to get it
=============

Either [download a binary][0] or [build the software yourself from sourcecode][5].

[![Build Status](https://travis-ci.org/MoriTanosuke/glacieruploader.svg?branch=split-into-modules)](https://travis-ci.org/MoriTanosuke/glacieruploader)

How to run
==========

You need a file named `aws.properties` with 2 lines:

    accessKey=your_aws_access_key
    secretKey=your_secret_key

When running the uploader, specify the path to this file with `--credentials /path/to/aws.properties`
or set the default in your configuration file. If you don't specify this option, the default is to
search for the file in javas `user.home` directory.

Command-line options
====================

    Option                                  Description                            
    ------                                  -----------                            
    -?, -h, --help                          display the help menu                  
    -a, --calculate <File>                  calculate hashsum for a file           
    -c, --create                            creates a new vault                    
    --credentials <File>                    path to your aws credentials file      
                                              (default: /home/carsten/aws.      
                                              properties)                          
    -d, --delete                            deletes an existing archive            
    -e, --endpoint                          URL of the amazon AWS endpoint where   
                                              your vault is                        
    -l, --list-inventory                    retrieve the inventory listing of a    
                                              vault                                
    -m, --multipartupload <File>            start uploading a new archive in       
                                              chuncks                              
    -o, --download                          download an existing archive           
    -p, --partsize [Integer]                sets the size of each part for         
                                              multipart uploads (default: 10485760)
    -r, --delete-vault                      deletes an existing vault
    -s, --list-vaults                       lists all available vaults 
    -t, --target <File>                     filename to store downloaded archive   
    -u, --upload <File>                     start uploading a new archive          
    -v, --vault                             name of your vault   

If you have issues with command line parameters containing an `-`, put the parameter in quotes (`"`).

Configuration file
==================

Available since 0.0.6

If you don't want to specify the options `credentials`, `vault` and `endpoint` every time, you can 
create a configuration file `.glacieruploaderrc` and enter the options there. The default is to search
for the configuration file in java `user.home` directory.

This is an example of the configuration file:

    credentials=/home/myuser/some/path/mysecretaws.properties
    endpoint=https://glacier.eu-west-1.amazonaws.com
    vault=myvaultname

Examples
========

Create vault
------------

Available since 0.0.4.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --create

Delete vault
------------

Available since 0.0.4.
Changed in 0.0.6.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --delete-vault

Upload archive
--------------

Available since 0.0.3.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --upload /path/to/my/file.zip
    
    Starting to upload file.zip...
    Using endpoint https://glacier.eu-west-1.amazonaws.com
    Starting upload of file.zip
    Uploaded archive j7UL7pH46FJGhoAxNVDsdjhHs_GLSKGLd12Dq44dfiyTciW6DSCQubctUFEZ4nKWPrJzv_YoxPVK_TfdAuMCxiQIE3_zEGDg84luI0-tzWMusdfjKHG2ILuhJhK5PySOOaw

This will return an archive id which you can use to retrieve the archive again later.

Multipart Upload archive
--------------

Available since 0.0.8.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --multipartupload /path/to/my/file.zip --partsize 8388608
    
    Multipart uploading upload file.zip...
    Using endpoint https://glacier.eu-west-1.amazonaws.com
    ArchiveID: j7UL7pH46FJGhoAxNVDsdjhHs_GLSKGLd12Dq44dfiyTciW6DSCQubctUFEZ4nKWPrJzv_YoxPVK_TfdAuMCxiQIE3_zEGDg84luI0-tzWMusdfjKHG2ILuhJhK5PySOOaw
    Part uploaded, checksum: 964a3dea958ad12959450dcc8f28acba0830989d1b8ae72442cebbe6f0d29e3e
    ...
    Uploaded archive j7UL7pH46FJGhoAxNVDsdjhHs_GLSKGLd12Dq44dfiyTciW6DSCQubctUFEZ4nKWPrJzv_YoxPVK_TfdAuMCxiQIE3_zEGDg84luI0-tzWMusdfjKHG2ILuhJhK5PySOOaw

This will return an archive id which you can use to retrieve the archive again later.

For a discussion about the *partsize* see 
https://forums.aws.amazon.com/message.jspa?messageID=482320

Download archive
----------------

Available since 0.0.4.
Changed in 0.0.5.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --download myarchiveid --target path/to/filename.zip

This will download the archive to a temporary location and append the prefix `glacier-` and the postfix `.dl`
to the file. *This will change later and you'll provide the file location.*

The option `--target` specifies an absolute or relative path to a file where the downloaded archive will be saved.

Delete archive
--------------

Available since 0.0.6

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --delete myarchiveid

Deletes the archive `myarchiveid`.

Calculate HASH for file
-----------------------

Available since 0.0.5.

    java -jar glacieruploader.jar --calculate /path/to/file.zip
    cd229f64d6a3aeb4f1bcbe953d8a7be00a077747b6d6a1001e65d14ef10c1706

This will calculate a TreeHash sum for the given file. The hash can be verified with the one calculated by Amazon.

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

Retrieve recent jobs
--------------------

Available since 0.1.1.

    java -jar glacieruploader.jar --endpoint https://glacier.eu-west-1.amazonaws.com --vault myvaultname --list-jobs
    Job ID: gtf3gLYYO4Hh7p_lImBT6VcHwUPp1cMpwWcbIa31rl9c82t6xKuUXiZa8tubAwbE0_gRBiKvlrx7S3MmjWrTv1234567
    Creation date: 2016-01-01T12:34:56.789Z
    Status: InProgress
    
    Job ID: 4TVy1rijcW4ezU5o2zJv8RzLrUkh8l8mffSJK4KB--fwyMPxhu0e3u8D5Ucw9Y2c0nyYbnTOlVKBXGiIz9wMp1234567
    Creation date: 2016-01-01T12:34:56.789Z
    Status: InProgress

List all vaults
-----------------

Available since 0.0.8

    java -jar glacieruploader.jar --endpoint eu-west-1 -s 
    CreationDate:		2012-08-22T06:33:34.574Z
    LastInventoryDate:	2014-12-05T16:38:22.250Z
    NumberOfArchives:	12
    SizeInBytes:		15358796835
    VaultARN:		arn:aws:glacier:eu-west-1:123456789:vaults/myvault

This is not working for me. What should I do?
=============================================

Go to my [Issues page][1] and add a new issue with a nice report about what's failing for you.
Try to provide enough information so I can help you out and fix the error for everyone. It 
might be a good idea to check for existing issues before reporting a new one.

And as always, you can [fork me and fix the error yourself][2]. :-)

License
=======

This project is distributed under [GNU GPL v3][3].


[0]: http://download.kopis.de.s3-website-eu-west-1.amazonaws.com/glacieruploader/index.html
[1]: https://github.com/MoriTanosuke/glacieruploader/issues
[2]: https://github.com/MoriTanosuke/glacieruploader/fork
[3]: http://www.gnu.org/licenses/gpl-3.0.html
[5]: http://blog.kopis.de/glacieruploader/howtobuild
[6]: https://github.com/MoriTanosuke/glacieruploader/
