--如果存在pkNum
if (redis.call('exists', KEYS[1])) == 1 then
    local currentNum = redis.call('get', KEYS[1])
    --当前pkNum在MAX到MIN之间，自增后直接返回
    if (tonumber(currentNum) <= tonumber(ARGV[2]) and tonumber(currentNum) >= tonumber(ARGV[3])) then
        return redis.call('incrby', KEYS[1], tonumber(ARGV[4]))
    --代表PK结束    
    else
        return currentNum
    end
else
    --如果不存在pkNum，则初始化pkNum
    redis.call('set', KEYS[1], tonumber(ARGV[1]))
    redis.call('EXPIRE', KEYS[1], 3600 * 12)
    --自增返回
    return redis.call('incrby', KEYS[1], tonumber(ARGV[4]))
end