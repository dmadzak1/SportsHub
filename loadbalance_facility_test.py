import time
from collections import Counter
from urllib.request import urlopen
import json

URL = "http://localhost:8082/test/facility-instance"
REQUEST_COUNT = 100

results = Counter()
errors = 0

start_time = time.perf_counter()

for i in range(REQUEST_COUNT):
    try:
        with urlopen(URL, timeout=10) as response:
            body = response.read().decode("utf-8")
            data = json.loads(body)

            port = str(data.get("port", "unknown"))
            results[port] += 1

    except Exception as ex:
        errors += 1
        print(f"Request {i + 1} failed: {ex}")

end_time = time.perf_counter()
total_time = end_time - start_time

print("Facility load balancing test")
print("============================")
print(f"URL: {URL}")
print(f"Total requests: {REQUEST_COUNT}")
print(f"Successful requests: {REQUEST_COUNT - errors}")
print(f"Failed requests: {errors}")
print(f"Total time: {total_time:.4f} seconds")
print(f"Average time per request: {total_time / REQUEST_COUNT:.4f} seconds")
print()

print("Requests per facility instance:")
for port, count in sorted(results.items()):
    print(f"Facility instance on port {port}: {count} requests")