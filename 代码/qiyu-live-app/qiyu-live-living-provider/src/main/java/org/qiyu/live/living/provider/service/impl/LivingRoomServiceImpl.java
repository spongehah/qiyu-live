package org.qiyu.live.living.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.living.interfaces.dto.LivingPkRespDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.provider.dao.mapper.LivingRoomMapper;
import org.qiyu.live.living.provider.dao.mapper.LivingRoomRecordMapper;
import org.qiyu.live.living.provider.dao.po.LivingRoomPO;
import org.qiyu.live.living.provider.dao.po.LivingRoomRecordPO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LivingRoomServiceImpl implements ILivingRoomService {

    @Resource
    private LivingRoomMapper livingRoomMapper;
    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = BeanUtil.copyProperties(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(new Date());
        livingRoomMapper.insert(livingRoomPO);
        return livingRoomPO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = livingRoomMapper.selectById(livingRoomReqDTO.getRoomId());
        if (livingRoomPO == null) {
            return false;
        }
        if (!livingRoomReqDTO.getAnchorId().equals(livingRoomPO.getAnchorId())) {
            return false;
        }
        LivingRoomRecordPO livingRoomRecordPO = BeanUtil.copyProperties(livingRoomPO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordPO.setStatus(CommonStatusEnum.INVALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        livingRoomMapper.deleteById(livingRoomPO.getId());
        return true;
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getId, roomId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        LivingRoomPO livingRoomPO = livingRoomMapper.selectOne(queryWrapper);
        return BeanUtil.copyProperties(livingRoomPO, LivingRoomRespDTO.class);
    }

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return null;
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        return null;
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return false;
    }
}
