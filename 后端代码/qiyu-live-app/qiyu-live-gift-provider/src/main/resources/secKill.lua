if (redis.call('exists', KEYS[1])) == 1 then
    local currentStock = redis.call('get', KEYS[1])
    if (tonumber(currentStock) >= tonumber(ARGV[1])) then
        return redis.call('decrby', KEYS[1], tonumber(ARGV[1]))
    else
        return -1
    end
    return -1
end