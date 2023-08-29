import socket
import threading
import sys

def handle(clientSocket, clientInfo):
    while True:
        try:
            valueOne = int(clientSocket.recv(1024).decode("utf-8"))
            valueTwo = int(clientSocket.recv(1024).decode("utf-8"))
            operator = clientSocket.recv(1024).decode("utf-8")
            print(f"{clientInfo} sended: {valueOne}, {operator} and {valueTwo}")

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
    print(f"[-] {clientInfo} disconnected")
    clientSocket.close()
                

def runServer(ip, port):
    socketServer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socketServer.bind((ip, port))

    socketServer.listen(20)
    
    print(f"[*] Server is running on {ip}:{port}")
    while True:
        try:
            clientSocket, clientInfo = socketServer.accept()
            print(f"[+] {clientInfo} connected.")
            
            clientThread = threading.Thread(target=handle, args=(clientSocket, clientInfo))
            clientThread.start()
        except:
            break
    print(f"[-] Server stopped")

if __name__ == "__main__":
    try:
        ip = sys.argv[1]
        port = int(sys.argv[2])
        runServer(ip, port)
    except:
        print("\nUsage: python server.py <ip-server> <port-server>\nExample: python server.py 127.0.0.1 1337\n")