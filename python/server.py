import socket
import threading
import sys
from colorama import Fore, Style


def handle(clientSocket, clientInfo):
    while True:
        try:
            valueOne = int(clientSocket.recv(1024).decode("utf-8"))
            valueTwo = int(clientSocket.recv(1024).decode("utf-8"))
            operator = clientSocket.recv(1024).decode("utf-8")
            print(Fore.CYAN + f"{clientInfo} sended: {valueOne}, {operator} and {valueTwo}")

            result = ""

            if operator in ["+", "-", "*"]:
                result = str(eval(f"{valueOne} {operator} {valueTwo}"))
            elif operator == "/":
                if valueTwo == 0:
                    result = "Error, cannot be divided by 0"
                else:
                    result = str(valueOne / valueTwo)
            else:
                result = "Invalid operator"
            clientSocket.send(result.encode("utf-8"))
        except Exception as error:
            print(error)
            clientSocket.send("Have some issues, bye bye!".encode("utf-8"))
            break
    print(Fore.YELLOW + f"[-] {clientInfo} disconnected")
    clientSocket.close()
                

def runServer(ip, port, clients):
    socketServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socketServer.bind((ip, port))

    socketServer.listen(clients)
    
    print(Fore.GREEN + f"[*] Server is running on {ip}:{port}")
    while True:
        try:
            clientSocket, clientInfo = socketServer.accept()
            print(Fore.CYAN + f"[+] {clientInfo} connected.")
            
            clientThread = threading.Thread(target=handle, args=(clientSocket, clientInfo))
            clientThread.start()
        except:
            break
    print(f"[-] Server stopped")

if __name__ == "__main__":
    try:
        ip = sys.argv[1]
        port = int(sys.argv[2])
        clients = int(sys.argv[3])
        runServer(ip, port, clients)
    except:
        print(Fore.YELLOW + "\nUsage: python server.py <ip-server> <port-server> <clients>\nExample: python server.py 127.0.0.1 1337 20\n")