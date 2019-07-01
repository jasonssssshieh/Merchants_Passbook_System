
sales_month
SELECT COUNT(VIN) AS Num_of_Sold_Month, EXTRACT(MONTH FROM Sale_date) AS Sale_Month
FROM Sell_transaction
GROUP BY Sale_Month;

sales_year
SELECT COUNT(VIN) AS Num_of_Sold_Year, EXTRACT(YEAR FROM Sale_date) AS Sale_Year
FROM Sell_transaction
GROUP BY Sale_Year;

total sales income_year
SELECT SUM(Sale_price) AS Total_Sales_Income_Year, EXTRACT(YEAR FROM Sale_date) AS Sale_Year
FROM Sell_transaction
GROUP BY Sale_Year;

total sales income_month
SELECT SUM(Sale_price) AS Total_Sales_Income_Month, EXTRACT(MONTH FROM Sale_date) AS Sale_Month
FROM Sell_transaction
GROUP BY Sale_Month;

salesperson: GROUP BY Salesperson_username


CREATE Veh_Buy_Table;

SELECT Buy.VIN AS  VIN, Vehicle.Vehicle_Type AS Vehicle_Type, COALESCE(Buy.Purchase_price, 0) AS Purchase_price,
        Buy.Purchase_condition AS Purchase_condition
FROM Buy_Transaction AS Buy
JOIN Vehicle
ON Vehicle.VIN = Buy.VIN

SELECT Vehicle_Type, AVG(Purchase_price) AS AveragePrice
FROM Veh_Buy_Table
GROUP BY Vehicle_Type

SELECT Purchase_condition, AVG(Purchase_price) AS AveragePrice
FROM Veh_Buy_Table
GROUP BY Purchase_condition



//CREATE Template Table: Sell_Buy_Table

SELECT Sell.VIN as VIN, EXTRACT(MONTH FROM Sell.Sale_date) AS Sale_Month, EXTRACT(YEAR FROM Sell.Sale_date) AS Sale_Year
      Sell.Sale_price AS Sale_price, Sell.Salesperson_username AS Salesperson_username
      EXTRACT(YEAR FROM Buy.Purchase_date) AS Purchase_year, EXTRACT(MONTH FROM Buy.Purchase_month) AS Purchase_month,
      Buy.Purchase_price AS Purchase_price
FROM Sell_transaction AS Sell
JOIN Buy_transaction AS Buy
ON Sell.VIN = Buy.VIN;



INSERT INTO `Repair`
VALUES(
'$enteredVIN', '$enteredRepair_Vendor_Name', '$enteredRepair_Vendor_Address',
'$enteredRepair_Vendor_Phone_Number', '$enteredRepair_Description',  '$enteredRepair_Start_Date',
'$enteredRepair_Cost', '$enteredNHTSA_Recall_Campagin_Number', '$enteredRepair_Status'
);




SELECT VIN, Repair_Vendor_Name, Repair_Vendor_Address, Repair_Vendor_Phone_Number,
Repair_Description, Repair_Start_Date, Repair_Cost, NHTSA_Recall_Campagin_Number
FROM `Repair` WHERE Repair.VIN = '$enteredVIN'


UPDATE `Repair`
SET VIN = '$enteredVIN',
         Repair_Vendor_Name = '$enteredRepair_Vendor_Name',
         Repair_Vendor_Address = 'enteredRepair_Vendor_Address',
         Repair_Vendor_Phone_Number  = 'enteredRepair_Vendor_Phone_Number',
         Repair_Description  = 'enteredRepair_Description',
         Repair_Start_Date  = 'enteredRepair_Start_Date',
         Repair_Cost  = 'enteredRepair_Cost',
         NHTSA_Recall_Campagin_Number  = 'enteredNHTSA_Recall_Campagin_Number',
         Repair_Status = 'enteredRepair_Status';



SELECT
 VIN, Repair_Vendor_Name, Repair_Vendor_Address, Repair_Vendor_Phone_Number,
 Repair_Description, Repair_Start_Date, Repair_Cost, NHTSA_Recall_Campagin_Number
FROM `Repair` WHERE Repair.VIN = '$enteredVIN';



DELETE FROM `Repair` WHERE Repair.VIN = '$enteredVIN' AND Repair.Start_Date = '$enteredStart_Date';



INSERT INTO `Vehicle`
VALUES(
'$enteredVIN', '$enteredVehicle_Type', '$enteredVehicle_Manufacturer', '$enteredModel_Name',
'$enteredModel_Year',  '$enteredVehicle_Color', '$enteredMileage', '$enteredSales_Price',
'$enteredVehicle_Description'
);


DELETE FROM `Vehicle` WHERE Vehicle.VIN = '$enteredVIN';


SELECT
VIN, Vehicle_Type, Vehicle_Manufacturer,
Model_Name, Model_Year, Color, Mileage, Vehicle_Description
FROM ` Vehicle` WHERE Vehicle.VIN = '$enteredVIN';


UPDATE `Vehicle`
SET VIN = '$enteredVIN',
         Vehicle_Type = '$enteredVehicle_Type ',
         Vehicle_Manufacturer = 'enteredVehicle_Manufacturer,
         Model_Name = 'enteredModel_Name',
         Model_Year  = 'enteredModel_Year',
         Color  = 'enteredColor',
         Mileage = 'enteredMileage',
         Vehicle_Description = 'enteredVehicle_Description';


INSERT' INTO `Recall`
   VALUES(
   '$enteredNHTSA_Recall_Campagin_Number',
   '$enteredRecall_Description', '$enteredRecall_Manufacture',
   );


DELETE FROM `Recall`
WHERE Recall.NHTSA_Recall_Campagin_Number = '$enteredNHTSA_Recall_Campagin_Number'
AND Recall.NHTSA_Recall_Campagin_Number NOT IN (
SELECT DISTINCT(NHTSA_Recall_Campagin_Number)
FROM Repair
WHERE Repair.NHTSA_Recall_Campagin_Number = '$enteredNHTSA_Recall_Campagin_Number'
);


Average Time In Inventory


SELECT Vehicle.Vehicle_Type AS Vehicle_Type, AVE(tbl.DateDiff) AS Average_Time_In_Inventory
FROM (
	SELECT sell.VIN AS VIN, DATEDIFF(day,buy.Purchase_date, sell.Sale_date) AS DateDiff
FROM `Sell_transaction` AS sell
LEFT JOIN Buy_transaction AS buy
ON sell.VIN = buy.VIN
WHERE buy.Purchase_date IS NOT NULL AND sell.Sale_date IS NOT NULL;
)tbl
LEFT JOIN `Vehicle`
ON Vehicle.VIN = tbl.VIN
GROUP BY Vehicle. Vehicle_Type
ORDER BY Vehicle. Vehicle_Type;



SELECT NHTSA_Recall_Campagin_Number, Recall_Description, Recall_Manufacture
FROM `Recall`
WHERE Recall. NHTSA_Recall_Campagin_Number = '$enteredNHTSA_Recall_Campagin_Number'

SELECT NHTSA_Recall_Campagin_Number, Recall_Description, Recall_Manufacture
FROM `Recall`
WHERE Recall. NHTSA_Recall_Campagin_Number = '$enteredNHTSA_Recall_Campagin_Number'

UPDATE `Recall`
SET NHTSA_Recall_Campagin_Number = '$enteredNHTSA_Recall_Campagin_Number',
         Recall_Description = '$enteredRecall_Description ',
         Recall_Manufacture = 'entered Recall_Manufacture';



CREATE TABLE Vehicle_Sales_Table
AS (
    SELECT Vehicle.VIN AS VIN, Vehicle.Vehicle_Type AS Vehicle_Type,
      COALESCE(Buy.Purchase_price, 0) as Purchase_price, Buy.Purchase_condtion
    FROM Vehicle
    JOIN Buy_transcation as Buy
    ON Buy.VIN = Vehicle.VIN
);

SELECT Vehicle_Type, [Purchase_condtion1], [Purchase_condtion2], [Purchase_condtion3]
FROM Vehicle_Sales_Table
PIVOT(
  AVG(Purchase_price) FOR Purchase_condtion IN [Purchase_condtion1], [Purchase_condtion2], [Purchase_condtion3]
)tbl;


CREATE TABLE Vehicle_Sales_Table
  AS (
    SELECT Vehicle.VIN AS VIN, Vehicle.Vehicle_Type AS Vehicle_Type,
           COALESCE(Buy.Purchase_price, 0) as Purchase_price, Buy.Purchase_condtion
    FROM Vehicle
      JOIN Buy_transcation as Buy
        ON Buy.VIN = Vehicle.VIN
  );

SELECT Vehicle_Type,
  AVG(
      CASE
      WHEN Purchase_condtion = 'Condition1'
        THEN Purchase_price
      ELSE NULL
      END
  ) As 'Condition1_AVG',
  AVG(
      CASE
      WHEN Purchase_condtion = 'Condition2'
        THEN Purchase_price
      ELSE NULL
      END
  ) As 'Condition2_AVG',
  AVG(
      CASE
      WHEN Purchase_condtion = 'Condition3'
        THEN Purchase_price
      ELSE NULL
      END
  ) As 'Condition3_AVG'
FROM Vehicle_Sales_Table
GROUP BY Vehicle_Type




SELECT CONCAT(ISNULL(last_name, ''), ISNULL(business_name, ''))
FROM (
       SELECT buy.customer_id as id,
              person.last_name as last_name,
              business.business_name as business_name
       FROM buy
         LEFT JOIN person
           ON buy.customer_id = person.customer_id
         LEFT JOIN business
           ON buy.customer_id = business.customer_id
     )tbl
WHERE tbl.id = $enteredId;


 SELECT CONCAT(ISNULL(person.last_name, ''), ISNULL(business.business_name, '')
 FROM buy
   LEFT JOIN person
     ON buy.customer_id = person.customer_id
   LEFT JOIN business
     ON buy.customer_id = business.customer_id
 WHERE buy.id = $enteredId;


