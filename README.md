CloudAid2
=========

Agregation of Linked USDL Cloud Services using Multi-Criteria Methods

Continuation of the CloudAid1 project [https://github.com/jorgearj/CloudAid]:

-New MCDM methods: ELECTRE III, PROMETHEE I, SMAA-2, SAW developed by the Decision Deck group.

-Real Service Offerings - Extracted using the LinkedUSDL pricing API [1] plus scraping. Currently, there's a total of 9280 Service Offerings from AmazonEC2 and Arsys. (Soon, Google Compute Engine will be added to the TripleStore along with diversified/specialized Service Offerings like Load Balacing/Databases/Caching/BigData.)

-Dynamic Price calculation -> Development and usage of the LinkedUSDL Pricing API.

-New aggregation algorithm that is able to handle incomparability and incomplete information. 


[1] https://github.com/jorgearj/USDLPricing_API
