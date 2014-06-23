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

--Aggregation Module: Completed.

---Graphic Interface development - Completed, source code [here](https://github.com/dguedesb/CloudAid2-GUI).

--- Communication is JSON based. The information exchanged between the CloudAid2 and user interface is kept in memory located under the respective folders.

Runnable version of the prototype [here](https://www.dropbox.com/s/qti1i5pd38wa1qu/CloudAid2.rar).

NOTE: GUI Requires [JAVA8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

Full dataset of services [here](https://www.dropbox.com/s/8zoz0107ky83mdf/ServiceVault.rar). Includes the modeling of the following services:

- [Amazon EC2 virtual machines(on demand, reserved and spot instances)](http://aws.amazon.com/ec2/).
- [Amazon RDS](http://aws.amazon.com/rds/).
- [Amazon Glacier](http://aws.amazon.com/glacier/).
- [Amazon Elastic Load Balancing](http://aws.amazon.com/elasticloadbalancing/).

Instructions:

1- Extract the Cloudaid2.rar file into a folder of your choice.

2- Open you console in the folder where you extracted the contents of the .rar file and type:
        
		a) java -jar CloudAid2.jar
		b) java -jar CloudAid2-GUI.jar

3- Choose one of the two examples to test the application or create your own!
