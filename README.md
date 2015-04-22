# DynamicDNS
A Dynamic DNS Solution to work with Digital Ocean.

I use this to make sure my machine always has it's public IP updated in a DNS record on my DigitalOcean account. It essentially does what DynDNS does, except for free.... Providing you have a DigitalOcean account... 

It works by the following process, some of the steps are completely arbitrary but I had my reasons for doing them:

1. Checks internet connectivity by doing a HTTP request to google and amazon. If both fail then we definitely don't have internet connectivity so there's no point going any futher.

2. Gets the current IP stored in the DNS record

3. Gets the computers current public IP by connecting to AWS which provides a handy tool for getting your public IP.

4. Compares the recorded IP in the DNS record with the current public IP and if they are different it updates the DNS record. 

I've set up as a cron job to run this every 5 minutes.

The package uses the JSON simple library from google for all things JSON:
https://code.google.com/p/json-simple/