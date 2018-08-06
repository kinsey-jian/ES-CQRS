package com.kinsey.product.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;

@Service
@AllArgsConstructor
@Slf4j
public class WorkerIdService {

    private final WorkerIdRepository workerIdRepository;


    Long getWorkerId() {

        String serviceKey = getServiceKey();

        WorkerId workerId = workerIdRepository.findByServiceKey(serviceKey);

        if (workerId != null) {
            return workerId.getId() % (SnowFlake.MAX_MACHINE_NUM + 1);
        }

        workerId = new WorkerId();
        workerId.setServiceKey(serviceKey);
        workerIdRepository.save(workerId);
        return workerId.getId() % (SnowFlake.MAX_MACHINE_NUM + 1);
    }

    public String getServiceKey() {
        InetAddress ia;
        byte[] mac = null;
        String hostAddress = null;
        try {
            //获取本地IP对象
            ia = InetAddress.getLocalHost();
            hostAddress = ia.getHostAddress();
            //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (mac == null) {
            return null;
        }
        //下面代码是把mac地址拼装成String
        StringBuilder macAddress = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                macAddress.append("-");
            }
            //mac[i] & 0xFF 是为了把byte转化为正整数
            String s = Integer.toHexString(mac[i] & 0xFF);
            macAddress.append(s.length() == 1 ? 0 + s : s);
        }

        //把字符串所有小写字母改为大写成为正规的mac地址并返回
        return hostAddress + ":" + macAddress.toString().toUpperCase();
    }

}
