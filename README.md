upbClient is a Java front end to the upbServer.
You must have a hobbyist (free) Pubnub account to use this client.
The upbClient and UpbServer applictions must be run on two independent
machines.  This is because Pubnub uses the IP address as the source
and destination address. Running both apps on one machine will result in
an addressing conflict.

The config.properties file must be updated prior to running program
