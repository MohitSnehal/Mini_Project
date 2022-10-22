import requests
import time
import csv
import json

data = {"statusCode":"ADS-200","status":"ok","message":"success","payload":{"testCaseType":"TEN_MILLION_AND_REPEATING","streamSize":10000000,"falsePositivesCount":{"18":[{"3":786137},{"4":786137},{"5":786137},{"6":786137},{"7":786137},{"8":786137}],"19":[{"3":785957},{"4":786127},{"5":786137},{"6":786137},{"7":786137},{"8":786137}],"20":[{"3":767595},{"4":781348},{"5":784882},{"6":785858},{"7":786095},{"8":786130}],"21":[{"3":599244},{"4":673559},{"5":722161},{"6":751857},{"7":768787},{"8":777364}],"22":[{"3":278573},{"4":332684},{"5":395167},{"6":461500},{"7":525108},{"8":580353}],"23":[{"3":76603},{"4":77847},{"5":86142},{"6":100001},{"7":119381},{"8":142764}]}}}
dict_data = []
index = 0
csv_columns = ['logNoOfBits','3','4','5', '6','7','8']
print("started execution")

for	testType in list(range(2, 3)):
	
	for i in range(18,24):
		row = []
		for j in range(0, 6):
			row.append(data['payload']['falsePositivesCount'][str(i)][j][str(j+3)])
		dict_data.append({
			'logNoOfBits':i,
			'3': row[0],
			'4': row[1],
			'5': row[2], 
			'6': row[3],
			'7': row[4],
			'8': row[5]
			})
csv_file = "bloom_filter" + ".csv"
try:
	with open(csv_file, 'w') as csvfile:
		writer = csv.DictWriter(csvfile, fieldnames=csv_columns)
		writer.writeheader()
		for data in dict_data:
			writer.writerow(data)
except IOError:
	print("I/O error")

