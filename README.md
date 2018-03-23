haw
===
A Java-FX GUI fake SMTP/POP3 server based on Dumbster.

Forked from [https://github.com/sbeitzel/dumbster/](https://github.com/sbeitzel/dumbster/)

Features
===
 * SMTP server that delivers to a local mailstore
 * POP3 server that reads the same mailstore that the SMTP server writes to
 * Allow for multiple instances of each kind of server, to simulate a larger email ecosystem
 
Version History
===
## Current
The current development version is 1.0.1-SNAPSHOT
 * Fix bug where displayed number of messages in mail stores didn't update
 * Add ability to paste a raw email into a form and insert that message into a mailbox


## Previous
### 1.0
 * Forked from dumbster development branch at commit 2dda522b7804fbdafcc7626412b131705216f927
 * Added a JavaFX UI
 * Save the running server configurations (but not the contents of the mail stores) at app shutdown, and restore them next time the app launches
 * `mvn package` creates a DMG installer for MacOS X
 

LICENSE
===
Under Apache 2.0 license.