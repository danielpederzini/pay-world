import random
import uuid

import requests
from datetime import datetime
from itertools import permutations

keysResponse = requests.get('http://localhost:8080/api/accounts/keys')

if keysResponse.status_code == 200:
    keys = keysResponse.json()
    print(f"Keys: {keys}")
else:
    print(f"GET request failed with status code {keysResponse.status_code}")

pairs = list(permutations(keys, 2))

for pair in pairs:
	for i in range(10):
	    json = {
		"uuid": str(uuid.uuid4()),
	        "senderKey": pair[0],
	        "receiverKey": pair[1],
	        "amount": random.uniform(1, 1000),
	        "createdAt": datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")
	    }

	    paymentResponse = requests.post(url="http://localhost:8081/api/payments", json=json)
	    print(f"[{paymentResponse.status_code}] {json.get("uuid")}")
