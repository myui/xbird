declare function local:params() {
    local:params() 
};
declare function local:test($x) {
    "a"
};
declare function local:f() {
    let $x := local:params()
    return local:test($x)
};

local:f()