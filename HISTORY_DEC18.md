# Update: Test Coverage for searchByDateRange

Date: December 18, 2025
Commit: pending

Summary:
- Purpose: Increase unit-test coverage for `EventController.searchByDateRange` and ensure controller null/validation branches are covered.
- Files changed: src/test/java/com/calendar/controller/EventControllerTest.java
- Key additions: tests for missing parameters (both missing, missing start, missing end), invalid date formats, same-day queries, single-event return, service-layer exception handling, large dataset scenario, and a direct controller invocation to cover the null-check branch.
- Fixes: corrected a bug in the large-dataset test where hour calculation produced invalid hour values.
- Result: All tests pass locally: 22/22 passing (as of December 18, 2025).

Notes:
- The new direct controller test calls `EventController.searchByDateRange(null, null)` to assert the controller returns HTTP 400 with the message `startDate and endDate parameters are required`.