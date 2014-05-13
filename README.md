CloudAid2
=========

Agregation of Linked USDL Cloud Services using Multi-Criteria Methods

Continuation of the CloudAid1 project [https://github.com/jorgearj/CloudAid]:

-New MCDM methods: ELECTRE III, PROMETHEE I, SMAA-2, SAW developed by the Decision Deck group.

-Real Service Offerings - Extracted using the LinkedUSDL pricing API [1] plus scraping. Currently, there's a total of 9280 Service Offerings from AmazonEC2 and Arsys. (Soon, Google Compute Engine will be added to the TripleStore along with diversified/specialized Service Offerings like Load Balacing/Databases/Caching/BigData.)

-Dynamic Price calculation -> Development and usage of the LinkedUSDL Pricing API.

-New aggregation algorithm that is capable of handling incomparability and incomplete information. 


[1] https://github.com/jorgearj/USDLPricing_API


---------------------------------------------------------- Current Status ---------------------------------------------------------- 

--TripleStore population: Completed

--Search Module: Completed

--Decision Module: Completed.

--Aggregation Module: Under Development

----Extract specialized service offering's data: Load Balances,Databases,Caching,..

------AmazonElastinc Loadbalancing - Done

------Amazon Glacier - Done

------Amazon RDS - Done

----Structure the MCDM output into a more suitable output for the problem at hand - Done

----Research about aggregation techniques/approaches - Ongoing

----Algorithm development - Ongoing

----Algorith testing
