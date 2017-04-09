Chizuru -- Amazon S3 compatible Object Storage Service Server component.
================

# API Supported

The design of Chizuru Server Component is based upon the Amazon S3 RESTful API, which is exactly much better and friendly for developers usage on Object Storage.

* GET Service
* DELETE Bucket
* GET Bucket (List Objects) Version 2
* GET Bucket location
* HEAD Bucket
* List Multipart Uploads
* PUT Bucket
* Delete Multiple Objects (POST Bucket)
* GET Object
* PUT Object
* Complete Multipart Upload
* Initiate Multipart Upload
* List Parts
* Upload Part


# Configuration

Currently, supported Configuration options for Chizuru Server Component are:

* `MAX_CHUNKSIZE` : The standard size set for chunk unit in physical storage on disk. Every chunk will not exeed configured size.
* `TRANSFER_BUFFERSIZE` : The size configuration which would be used when IO buffer for transferring is to be allocated.

# Including

* There are amazon signature testing materials included in the testing package, downloading from [Signature Version 4 Test Suite](https://docs.aws.amazon.com/ja_jp/general/latest/gr/signature-v4-test-suite.html).
* There is a list as a proposals related to the problems of design on this component, as a scatch version of best practice in the future.