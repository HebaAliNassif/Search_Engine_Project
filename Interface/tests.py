import re
from concurrent.futures import ThreadPoolExecutor
import time
# if __name__ == "__main__":
#     string = ''' .call.
#         my name is Alfred,
#         I am seven years old, call me on
#         .callly.  but [call
#     '''
#     pattern = f"(.*[(-*.])(call)(\W.*)"
#     res = re.search(pattern, string, re.DOTALL)
#     a, b, c = res.groups()

#     print(a)
#     print(b)
#     print(c)


# def printChar(a):
#     print(char)


# a = ['a', 'b', 'c', 'd']
# z = zip(a, range(len(a)))
# for i in z:
#     print(i)
# # if __name__ == "__main__":
# #     with ThreadPoolExecutor() as exec:
# #         exec.map(printChar, zi)

s = "hello this is memorial from the loving automati callsds if you can"
call = "call"
pattern = re.compile(f"\\b{call}\\w*")
res = re.search(pattern, s)
print(res)
