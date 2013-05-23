# libreChat #
A chat client written in Android based on the IRC protocol [IETF RFC 2810](http://http://tools.ietf.org/html/rfc2810).
Supports simultaneous multichat and multiserver connections.
See important note below: current version (alpha) only works with ngIRCd.

## Usage

### Step 1

Connect to an IRC server from *Connect* tab:
* Configure IRC server (IP, port, servername and nickname)
* A new server can be added with the button *Add new Server*. Existing servers can be editted with the green arrow bottom.
* Switch to *ON* to connect. When green status, the client is successfully connected

### Step 2

Select group/channel on *Groups* tab:
* Groups are shown after a successful server connection
* If no channels shown, make sure you are connected to a server first
* Select one or multiple groups/channels to join

### Step 3

Click on *Go To Chats* button, and start chatting !

### File transfer (DCC) 

[DCC/CTCP](http://www.irchelp.org/irchelp/rfc/ctcpspec.html) is supported to send/transfer files from other IRC clients.
Current version only supports file reception.
Received files are stored on the SD card (ex. /mnt/sdcard or similar)

## Android release
libreChat has been developed with Android 4.2 release

## Important note
This is an early version of libreChat which only works with ngIRCd servers.
ngIRCd is a free, portable and lightweight Internet Relay Chat server for small networks, developed under the GNU General Public License (GPL).
ngIRCd can be downloaded from [here](http://ngircd.barton.de/)

## License
GNU GPL v3

## Screenshots

-![alt text](https://lh4.googleusercontent.com/IYzUIbczzQziOzJED7KI2u_yCL1YAKgK9kRNZ_7Ic-E=w124-h207-p-no)
-![alt text](https://lh3.googleusercontent.com/MwUM5aghoWHERZaGi5kChdHa41fY8X8WwoxTRqInvjA=w124-h207-p-no)
-![alt text](https://lh5.googleusercontent.com/XfoIrb1qEODfw1ngVc-kdioN-JeNp7qAcZnzGbBc1Sg=w124-h207-p-no)
-![alt text](https://lh3.googleusercontent.com/XiWzigQN2WgbsLy3h91eMsqQohIVfRirNxMNWDdGOr8=w124-h207-p-no)
-![alt text](https://lh6.googleusercontent.com/e5Plvoxltrw7YfKz3pCEkSD5MEFi4FqgIN9FLbMfGJQ=w124-h207-p-no)
