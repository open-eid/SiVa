Known Issues
============

-   After starting up the server, users need to wait for approximately
    two minutes before issuing requests to the server. (Otherwise, the
    requests may fail to validate.) To know how long to wait, look at
    the application output log and wait until it contains the following
    line: "--&gt; run(): END LOADING".\
    *Technical details: the TSL loading code has a known issue that has
    been reported to the developers of a library used by the current
    project:
    see *[*https://esig-dss.atlassian.net/browse/DSS-732*](https://esig-dss.atlassian.net/browse/DSS-732)*.
    Before the said TSL loading starts, the following will be printed
    into the log: "--&gt; run(): START LOADING"; later, when the loading
    finishes, it will print "--&gt; run(): END LOADING", after which it
    will be ok to issue requsts to the server.*

-   Signatures may fail to validate at 3:00 AM at night, for up to few
    minutes.\
    *Technical details: TSL reloading has a similar issue as TSL loading
    during startup: during the start of reload the code discards
    existing certificates cache, which may result in validation failures
    when the corresponding CA certificates are not yet reloaded. Current
    workaround is that the reload process gets triggered only once per
    day, during low traffic hours: every day at night, at 3:00 AM.
    Reported
    issue: *[*https://esig-dss.atlassian.net/browse/DSS-740*](https://esig-dss.atlassian.net/browse/DSS-740)*.*

-   Signature validation may take over 3 seconds of time.\
    *Technical details: on one test machine, a 2MB PDF file validation
    first request processing duration (after server startup) is about
    \~4,8 sec and following requests \~3 sec, for 90 KB file the
    validation duration statistics are practically the same. Reported
    issue: *[*https://esig-dss.atlassian.net/browse/DSS-742*](https://esig-dss.atlassian.net/browse/DSS-740)*.*

-   TSL loading connection timeout is 60 seconds. When a TSL server of a
    member state is not responding, it may take a long time to reach
    connection timeout (60 seconds). This issue is reported to DSS
    developers at: <https://esig-dss.atlassian.net/browse/DSS-720>.

-   No warnings are shown for signatures made with SHA-1. This has been
    reported to DSS developers
    at: https://esig-dss.atlassian.net/browse/DSS-760.


