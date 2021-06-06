import re

if __name__ == "__main__":
    string = ''' .call.
        my name is Alfred,
        I am seven years old, call me on
        .callly.  but [call
    '''
    pattern = f"(.*[(-*.])(call)(\W.*)"
    res = re.search(pattern, string, re.DOTALL)
    a, b, c = res.groups()

    print(a)
    print(b)
    print(c)
