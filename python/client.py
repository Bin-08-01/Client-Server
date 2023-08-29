import socket
import sys 
from colorama import Fore, Back, Style, init

def connectServer(ip, port):
    try:
        clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        clientSocket.connect((ip, port))
    except:
        print(Fore.RED + f"[-] Can't connect to {ip}:{port}")
        return

    while True:
        try:
            valueOne = str(input(Style.RESET_ALL + "[*] Enter value of a first number: "))
            clientSocket.send(valueOne.encode("utf-8"))

            valueTwo = str(input("[*] Enter value of a second number: "))
            clientSocket.send(valueTwo.encode("utf-8"))

            operator = str(input("[*] Enter operator ('+', '-', '*', '/'): "))
            clientSocket.send(operator.encode("utf-8"))

            result = clientSocket.recv(1024).decode("utf-8")
            print(Fore.CYAN + f"[+] Result: {result}")

            option = input(Fore.RED + "\n[*] Do you want continue? (Type y/n): ")
            if option != "y":
                break
        except:
            print(Fore.RED + "[-] Server unresponsive, disconnected!")
            break
    clientSocket.close()

if __name__ == "__main__":
    try:
        ip = sys.argv[1]
        port = int(sys.argv[2])
        connectServer(ip, port)
    except:
        print(Fore.YELLOW + "\nUsage: python client.py <ip-server> <port>\nExample: python client.py 127.0.0.1 1337")