# Residence Nil Rate Band Calculator Frontend

This service provides a frontend for the [residence nil-rate band](https://www.gov.uk/guidance/inheritance-tax-residence-nil-rate-band) calculator service.

### Requirements 
This service is written in Scala and Play 3.0, so needs at least a JRE to run.

### Running locally
To run the service locally:

    sbt 'run 7111'

You will also need to have the [residence nil-rate band calculator](https://github.com/hmrc/residence-nil-rate-band-calculator) service running.

You can start this manually, or use the following [service manager](https://github.com/hmrc/sm2) command:

    sm2 -start RNRB

To run dependencies for the service, the following command:

    sm2 -start RNRB_ALL

#### Test Coverage
To run the test coverage suite

 `sbt clean coverage test coverageReport`

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    