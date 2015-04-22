# DynamicDNS
A Dynamic DNS Solution to work with Digital Ocean.

I use this to make sure my machine always has it's public IP updated in a DNS record on my DigitalOcean account. It essentially does what DynDNS does, except for free.... Providing you have a DigitalOcean account... 

It works by the following process, some of the steps a kind of arbitrary but I had my own personal reasons for doing them:

1. Checks internet connectivity by doing a HTTP request to google and amazon. If both fail then we definitely don't have internet connectivity so there's no point going any futher.

2. Gets the current IP stored in the DNS record

3. Gets the computers current public IP by connecting to AWS which provides a handy tool for getting your public IP.

4. It compares the recorded IP with your current public IP and if they are different it updates the DNS record. 

I've set up as a cron job to run every 5 minutes.