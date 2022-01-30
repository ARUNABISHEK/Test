list = []


def push(data):
    list.append(data)


def pop():
    if len(list) == 0:
        print("List is empty...")
    else:
        list.pop()


def display():
    for i in range(0, len(list)):
        print(list[i])


def peek():
    if len(list) != 0:
        return list[len(list) - 1]
    print("List is empty...")


push(10)
push(20)
display()
pop()
print("Top : " + str(peek()))
