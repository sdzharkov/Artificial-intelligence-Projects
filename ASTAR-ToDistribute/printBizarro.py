
def bizarro(i,j):
	return "{0:.2f}".format(1.0*(i/(j+1)))

def main():
	for i in range(5):
		print(i,". ",end="")
		for j in range(5):
			try:
				print(bizarro(abs(i),abs(j))," ",end="")
			except ZeroDivisionError as e:
				print("e ",end="")
		print("\n",end="")


if __name__ == '__main__':
	main()