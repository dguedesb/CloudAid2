CloudAid2
=========

Agregation of Linked USDL Cloud Services using Multi-Criteria Methods



- Augmented version of the [CloudAid1 prototype](https://github.com/jorgearj/CloudAid). Includes:

- New MCDM methods, hosted by [Decision Deck group](http://www.decision-deck.org/project/):

 - ELECTRE III

 - PROMETHEE I

 - SMAA-2
 
 - SAW
 
- New cloud services repository with the following use cases:

 - [Amazon EC2 virtual machines(on demand, reserved and spot instances)](http://aws.amazon.com/ec2/).
 - [Amazon RDS](http://aws.amazon.com/rds/).
 - [Amazon Glacier](http://aws.amazon.com/glacier/).
 - [Amazon Elastic Load Balancing](http://aws.amazon.com/elasticloadbalancing/).


- Includes the [Linked USDL Pricing API](https://github.com/jorgearj/USDLPricing_API) for dynamic price calculation.

- New graph-theory based aggregation algorithm that can deal incomparability and incomplete information on Multi-Criteria Decision Problems. 

- New JavaFX (Graphical User Interface)(https://github.com/dguedesb/CloudAid2-GUI)  which establishes a communication with the server side of the application through JSON "requests"


Prototype's runnable:
==

Prototype can be downloaded from [here](https://www.dropbox.com/s/qti1i5pd38wa1qu/CloudAid2.rar).

NOTE: GUI Requires [JAVA8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


Instructions:

1- Extract the Cloudaid2.rar file into a folder of your choice.

2- Open you console in the folder where you extracted the contents of the .rar file and type:
        
		a) java -jar CloudAid2.jar
		b) java -jar CloudAid2-GUI.jar

3- Choose one of the two examples from History to test the application or create your own!




Dataset
==

The full dataset of services can be downloaded [here](https://www.dropbox.com/s/8zoz0107ky83mdf/ServiceVault.rar). 

These descriptions were created by the [ServiceGatherer](https://github.com/dguedesb/CloudAid2-ServiceGatherer) application which combines scrapping/parsing with the Linked USDL Pricing API, to extract and structure real services information.


