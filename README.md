# Residence Nil Rate Band Calculator Frontend
..
[![Build Status](https://travis-ci.org/hmrc/residence-nil-rate-band-calculator-frontend.svg?branch=master)](https://travis-ci.org/hmrc/residence-nil-rate-band-calculator-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/residence-nil-rate-band-calculator-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/residence-nil-rate-band-calculator-frontend/_latestVersion)

This service provides a frontend for the [residence nil-rate band](https://www.gov.uk/guidance/inheritance-tax-residence-nil-rate-band) calculator service.

### Requirements 
This service is written in Scala and Play 3.0, so needs at least a JRE to run.

### Running locally
To run the service locally:

    sbt 'run 7111'

You will also need to have the [residence nil-rate band calculator](https://github.com/hmrc/residence-nil-rate-band-calculator) service running.

You can start this manually, or use the following [service manager](https://github.com/hmrc/service-manager) command:

    sm --start RNRB

To run dependencies for the service, use one of the following commands:

    sm --start RNRB_ALL -r
    sm --start RNRB_DEP -r (only starts dependencies.)

#### Test Coverage
To run the test coverage suite

 `sbt clean coverage test coverageReport`

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    