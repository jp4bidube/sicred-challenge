import requests
import random
import concurrent.futures
import time
import json
import os
import sys

# Configurações
API_URL = os.getenv("API_URL", "http://localhost:8080")
TOTAL_VOTES = 1000
CONCURRENCY = 50    # Quantidade de threads simultâneas

def wait_for_api():
    """Espera a API estar disponível antes de iniciar."""
    print(f"Waiting for API at {API_URL}...")
    retries = 30
    for i in range(retries):
        try:
            response = requests.get(f"{API_URL}/v1/agendas")
            if response.status_code == 200:
                print("API is ready!")
                return
        except requests.exceptions.ConnectionError:
            pass

        print(f"API not ready yet. Retrying in 2s... ({i+1}/{retries})")
        time.sleep(2)

    print("API failed to start. Exiting.")
    sys.exit(1)

def generate_cpf():
    """Gera um CPF válido para teste."""
    def calculate_digit(digits):
        s = sum(d * w for d, w in zip(digits, range(len(digits) + 1, 1, -1)))
        r = 11 - (s % 11)
        return 0 if r >= 10 else r

    cpf = [random.randint(0, 9) for _ in range(9)]
    cpf.append(calculate_digit(cpf))
    cpf.append(calculate_digit(cpf))
    return "".join(map(str, cpf))

def create_agenda():
    print("Creating Agenda...")
    response = requests.post(f"{API_URL}/v1/agendas", json={"title": f"Load Test Agenda {time.time()}"})
    if response.status_code == 201:
        agenda = response.json()
        print(f"Agenda Created: {agenda['id']} - {agenda['title']}")
        return agenda['id']
    else:
        print(f"Failed to create agenda: {response.text}")
        exit(1)

def open_session(agenda_id):
    print(f"Opening Session for Agenda {agenda_id}...")
    response = requests.post(f"{API_URL}/v1/agendas/{agenda_id}/open?minutes=1")
    if response.status_code == 200:
        print("Session Opened successfully!")
    else:
        print(f"Failed to open session: {response.text}")
        exit(1)

def cast_vote(agenda_id):
    cpf = generate_cpf()
    choice = random.choice(["YES", "NO"])
    payload = {
        "associateId": cpf,
        "choice": choice
    }

    try:
        start_time = time.time()
        response = requests.post(f"{API_URL}/v1/agendas/{agenda_id}/votes", json=payload)
        elapsed = time.time() - start_time

        return {
            "status": response.status_code,
            "time": elapsed,
            "cpf": cpf
        }
    except Exception as e:
        return {"status": "ERROR", "error": str(e)}

def run_load_test():
    wait_for_api()

    agenda_id = create_agenda()
    open_session(agenda_id)

    print(f"\nStarting Load Test: {TOTAL_VOTES} votes with {CONCURRENCY} threads...")
    start_time = time.time()

    results = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=CONCURRENCY) as executor:
        futures = [executor.submit(cast_vote, agenda_id) for _ in range(TOTAL_VOTES)]
        for future in concurrent.futures.as_completed(futures):
            results.append(future.result())

    total_time = time.time() - start_time

    # Analisar resultados
    success = sum(1 for r in results if r['status'] == 202)
    failures = sum(1 for r in results if r['status'] != 202)
    avg_time = sum(r['time'] for r in results if isinstance(r.get('time'), float)) / len(results) if results else 0

    print("\n--- Load Test Results ---")
    print(f"Total Time: {total_time:.2f} seconds")
    print(f"Throughput: {TOTAL_VOTES / total_time:.2f} req/s")
    print(f"Average Latency: {avg_time * 1000:.2f} ms")
    print(f"Success (202 Accepted): {success}")
    print(f"Failures: {failures}")

    if failures > 0:
        print("Sample failures:")
        for r in results:
            if r['status'] != 202:
                print(r)
                break

    print(f"\nCheck results at: {API_URL}/v1/agendas/{agenda_id}/result (wait for session to close)")

if __name__ == "__main__":
    run_load_test()
