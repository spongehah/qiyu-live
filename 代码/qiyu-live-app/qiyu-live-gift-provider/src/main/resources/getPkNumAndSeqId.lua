-- 1.参数列表
-- 1.1.pkNum的key
local pkNumKey = ARGV[1]
-- 1.2.price
local price = ARGV[2]

-- 2
local pkNum = redis.call("incr", pkNumKey, price)

if pkNum == nil then
    return nil
end

return tonumber(pkNum)