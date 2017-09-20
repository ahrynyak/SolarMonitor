package cn.com.voltronic.solar.comusbprocessor;

import cn.com.voltronic.solar.beanbag.BeanBag;
import cn.com.voltronic.solar.beanbag.P18BeanBag;
import cn.com.voltronic.solar.communicate.IComUSBHandler;
import cn.com.voltronic.solar.communicate.ICommunicateDevice;
import cn.com.voltronic.solar.control.P18ComUSBControlModule;
import cn.com.voltronic.solar.data.bean.Capability;
import cn.com.voltronic.solar.data.bean.ConfigData;
import cn.com.voltronic.solar.data.bean.DataBeforeFault;
import cn.com.voltronic.solar.data.bean.DefaultData;
import cn.com.voltronic.solar.data.bean.MachineInfo;
import cn.com.voltronic.solar.data.bean.ProtocolInfo;
import cn.com.voltronic.solar.data.bean.WorkInfo;
import cn.com.voltronic.solar.processor.AbstractProcessor;
import cn.com.voltronic.solar.protocol.IProtocol;
import cn.com.voltronic.solar.system.GlobalProcessors;
import cn.com.voltronic.solar.util.DateUtils;
import cn.com.voltronic.solar.util.VolUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;










public class P18ComUSBProcessor
  extends AbstractComUSBProcessor
{
  private static Logger logger = Logger.getLogger(P18ComUSBProcessor.class);
  
  public static Lock lock = new ReentrantLock();
  public int _preparalleltype = 0;
  public boolean bfirsttime = true;
  
  private Object query_day = new Integer(1);
  private Object query_month = new Integer(1);
  private Object query_year = new Integer(1);
  private Object query_tatal = new Integer(1);
  
  private static final String ACTION_QUERY_MACHINE_INFO = "qryMachineInfo";
  
  private static final String ACTION_QUERY_CONFIG_DATA = "qryConfigData";
  
  private static final String GAP = ",";
  private static final double UNIT_10 = 10.0D;
  private Map<String, String> batteryType = new HashMap();
  private Map<String, String> inputVoltageType = new HashMap();
  private Map<String, String> outputSourceType = new HashMap();
  private Map<String, String> chargerSourceType = new HashMap();
  private Map<String, String> machineType = new HashMap();
  private Map<String, String> topologyType = new HashMap();
  private Map<String, String> solarPriorityType = new HashMap();
  private Map<String, String> workModeType = new HashMap();
  private Map<String, String> outputModelType = new HashMap();
  private Map<String, String> enable = new HashMap();
  private Map<String, String> regulationType = new HashMap();
  
  public P18ComUSBProcessor(ICommunicateDevice handler, IProtocol protocol)
  {
    super(handler, protocol);
    init();
  }
  
  private void init()
  {
    batteryType.put("0", "AGM");
    batteryType.put("1", "Flooded");
    batteryType.put("2", "User");
    
    inputVoltageType.put("0", "Appliance");
    inputVoltageType.put("1", "UPS");
    
    outputSourceType.put("0", "Solar-Utility-Battery");
    outputSourceType.put("1", "Solar-Battery-Utility");
    
    chargerSourceType.put("0", "Solar first");
    chargerSourceType.put("1", "Solar and Utility");
    chargerSourceType.put("2", "Solar only");
    
    machineType.put("0", "Off-grid");
    machineType.put("1", "Hybrid");
    
    topologyType.put("0", "Transformerless");
    topologyType.put("1", "Transformer");
    
    solarPriorityType.put("0", "Battery-Load-Utility");
    solarPriorityType.put("1", "Load-Battery-Utility");
    
    workModeType.put("00", "Power On Mode");
    workModeType.put("01", "Standby Mode");
    workModeType.put("02", "Bypass Mode");
    workModeType.put("03", "Battery Mode");
    workModeType.put("04", "Fault Mode");
    workModeType.put("05", "Hybrid Mode");
    
    outputModelType.put("0", "Single");
    outputModelType.put("1", "Parallel output");
    outputModelType.put("2", "Phase R of 3 phase output");
    outputModelType.put("3", "Phase S of 3 phase output");
    outputModelType.put("4", "Phase T of 3 phase output");
    
    enable.put("0", "Disable");
    enable.put("1", "Enable");
    
    regulationType.put("00", "India");
    regulationType.put("01", "Germany");
  }
  
  public String getDeviceMode()
  {
    return null;
  }
  
  protected void initBeanBag()
  {
    _beanbag = new P18BeanBag();
  }
  
  protected void initControlModule()
  {
    _control = new P18ComUSBControlModule(getHandler(), 
      (ConfigData)_beanbag.getBean("configdata"), 
      (Capability)_beanbag.getBean("capability"));
  }
  
  public void initProtocol()
  {
    ProtocolInfo info = (ProtocolInfo)getBeanBag().getBean("protocolinfo");
    info.setProdid(_protocol.getProtocolID());
    info.setBaseInfo(_protocol.getBaseInfo());
    info.setProductInfo(_protocol.getProductInfo());
    info.setRatingInfo(_protocol.getRatingInfo());
    info.setMoreInfo(_protocol.getMoreInfo());
    info.setMpptTrackNumber(_protocol.getMpptTrackNumber());
    try {
      info.setSerialno(_protocol.getSerialNo());
    } catch (Exception e) {
      logger.error("initProtocol Func -->" + e.getMessage());
    }
  }
  

  public boolean pollQuery()
  {
    WorkInfo workInfo = (WorkInfo)getBeanBag().getBean("workinfo");
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    workInfo.setProdid(_protocol.getProtocolID());
    workInfo.setSerialno(_protocol.getSerialNo());
    if (currenttime != null) {
      workInfo.setCurrentTime(currenttime.getTime());
    }
    
    if (bfirsttime) {
      _preparalleltype = _paralleltype;
      bfirsttime = false;
    }
    else if (_preparalleltype != _paralleltype) {
      close();
      return false;
    }
    

    if (_paralleltype != 0) {
      try
      {
        lock.lock();
        return pollQueryParallel();
      } catch (Exception e) {
        lock.unlock();
        return false;
      } finally {
        lock.unlock();
      }
    }
    try
    {
      pGS(workInfo, handler);
      pMOD(workInfo, handler);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return true;
  }
  
  protected void pGS(WorkInfo workInfo, IComUSBHandler handler)
  {
    String pGS = handler.excuteCommand("GS", true);
    if (isEmpty(pGS)) {
      return;
    }
    String[] gs = pGS.split(",");
    double gridVoltage = VolUtil.parseDouble(gs[0]) / 10.0D;
    double gridFrequency = VolUtil.parseDouble(gs[1]) / 10.0D;
    double acOutputVoltage = VolUtil.parseDouble(gs[2]) / 10.0D;
    double acOutputFrequency = VolUtil.parseDouble(gs[3]) / 10.0D;
    int acOutputApperentPower = VolUtil.parseInt(gs[4]);
    int acOutputActivePower = VolUtil.parseInt(gs[5]);
    int outputLoadPercent = VolUtil.parseInt(gs[6]);
    double batteryVoltage = VolUtil.parseDouble(gs[7]) / 10.0D;
    double batteryVoltageFromSCC1 = VolUtil.parseDouble(gs[8]) / 10.0D;
    double batteryVoltageFromSCC2 = VolUtil.parseDouble(gs[9]) / 10.0D;
    double disChargingCurrent = VolUtil.parseDouble(gs[10]);
    double chargingCurrent = VolUtil.parseDouble(gs[11]);
    int batteryCapacity = VolUtil.parseInt(gs[12]);
    int heatSinkTemperature = VolUtil.parseInt(gs[13]);
    int mpptChargerTemperature1 = VolUtil.parseInt(gs[14]);
    int mpptChargerTemperature2 = VolUtil.parseInt(gs[15]);
    
    int pvInputPower1 = VolUtil.parseInt(gs[16]);
    int pvInputPower2 = VolUtil.parseInt(gs[17]);
    double pvInputVoltage1 = VolUtil.parseDouble(gs[18]) / 10.0D;
    double pvInputVoltage2 = VolUtil.parseDouble(gs[19]) / 10.0D;
    
    String settingValueState = gs[20];
    String pv1WorkStatus = gs[21];
    String pv2WorkStatus = gs[22];
    String loadConnection = gs[23];
    String batteryStatus = gs[24];
    String invDirection = gs[25];
    String lineDirection = gs[26];
    String localParallelID = gs[27];
    
    setParallKey(VolUtil.parseInt(localParallelID));
    
    workInfo.setGridVoltageR(gridVoltage);
    workInfo.setGridFrequency(gridFrequency);
    workInfo.setAcOutputVoltageR(acOutputVoltage);
    workInfo.setAcOutputFrequency(acOutputFrequency);
    workInfo.setAcOutputApperentPowerR(acOutputApperentPower);
    workInfo.setAcOutputActivePowerR(acOutputActivePower);
    workInfo.setOutputLoadPercent(outputLoadPercent);
    
    workInfo.setBatteryVoltage(batteryVoltage);
    workInfo.setBatteryVoltageFromSCC1(batteryVoltageFromSCC1);
    workInfo.setBatteryVoltageFromSCC2(batteryVoltageFromSCC2);
    workInfo.setDisChargingCurrent(disChargingCurrent);
    workInfo.setChargingCurrent(chargingCurrent);
    workInfo.setBatteryCapacity(batteryCapacity);
    
    workInfo.setHeatSinkTemperature(heatSinkTemperature);
    workInfo.setMpptChargerTemperature1(mpptChargerTemperature1);
    workInfo.setMpptChargerTemperature2(mpptChargerTemperature2);
    
    workInfo.setMaxTemperature(workInfo.getHeatSinkTemperature());
    
    workInfo.setPvInputPower1(pvInputPower1);
    workInfo.setPvInputPower2(pvInputPower2);
    workInfo.setPvInputVoltage1(pvInputVoltage1);
    workInfo.setPvInputVoltage2(pvInputVoltage2);
    
    workInfo.setPv1WorkStatus(pv1WorkStatus);
    workInfo.setPv2WorkStatus(pv2WorkStatus);
    

    if ((workInfo.getPv1WorkStatus().equals("2")) || (workInfo.getPv2WorkStatus().equals("2"))) {
      workInfo.setPvLoss(false);
    } else {
      workInfo.setPvLoss(true);
    }
    

    if (loadConnection.equals("0")) {
      workInfo.setHasLoad(false);
    } else {
      workInfo.setHasLoad(true);
    }
    workInfo.setBatteryStatus(batteryStatus);
    workInfo.setInvDirection(invDirection);
    workInfo.setLineDirection(lineDirection);
  }
  
  protected void pMOD(WorkInfo workInfo, IComUSBHandler handler)
  {
    String pMOD = handler.excuteCommand("MOD", true);
    if (isEmpty(pMOD)) {
      return;
    }
    String workMode = (String)workModeType.get(pMOD);
    workInfo.setWorkMode(workMode);
  }
  
  public boolean pollQueryParallel()
  {
    boolean result = true;
    boolean bParentLoss = true;
    int parall_i = 0;
    ArrayList<String> curList = new ArrayList();
    ArrayList<String> delList = new ArrayList();
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    try {
      String[] prin;
      for (parall_i = 0; parall_i < _parallelnum; parall_i++)
      {

        String qPRIn = handler.excuteCommand("PRI" + parall_i, true);
        if (!isEmpty(qPRIn))
        {

          prin = qPRIn.split(",");
          int existent = VolUtil.parseInt(prin[0]);
          if (existent != 0)
          {

            int validLength = VolUtil.parseInt(prin[1]);
            String serial = prin[2];
            serial = serial.substring(0, validLength);
            curList.add(serial);
            if (subMap.containsKey(serial)) {
              ParallSubProcessor processor = (ParallSubProcessor)subMap.get(serial);
              processor.setParallKey(parall_i);
              pPGSn(parall_i, processor, handler);
              String oldKey = processor.processorKey();
              
              if (!processor.reGenProcesorKey().equalsIgnoreCase(oldKey)) {
                GlobalProcessors.removeProcessor(oldKey);
                GlobalProcessors.addProcessor(processor.processorKey(), processor);
              }
              
            }
            else if (serial.equalsIgnoreCase(getSerialNo())) {
              setParallKey(parall_i);
              pPGSn(parall_i, this, handler);
              String oldKey = processorKey();
              if (!reGenProcesorKey().equalsIgnoreCase(oldKey)) {
                GlobalProcessors.removeProcessor(oldKey);
                GlobalProcessors.addProcessor(processorKey(), this);
              }
              bParentLoss = false;
            } else {
              ParallSubProcessor processor = new ParallSubProcessor(this, new P18BeanBag());
              processor.setDeviceName(getDeviceName());
              processor.setSerialNo(serial);
              processor.setParallKey(parall_i);
              pPGSn(parall_i, processor, handler);
              subMap.put(serial, processor);
              processor.saveDevice();
              GlobalProcessors.addProcessor(processor.processorKey(), processor);
            }
          }
        } }
      for (Map.Entry<String, ParallSubProcessor> entry : subMap.entrySet()) {
        if (curList.indexOf(entry.getKey()) < 0) {
          ((ParallSubProcessor)entry.getValue()).close();
          delList.add((String)entry.getKey());
        }
      }
      for (String key : delList) {
        subMap.remove(key);
      }
      if (bParentLoss) {
        close();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      result = false;
    }
    return result;
  }
  

  public boolean pollQueryStatus()
  {
    WorkInfo workInfo = (WorkInfo)getBeanBag().getBean("workinfo");
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    
    workInfo.setProdid(_protocol.getProtocolID());
    workInfo.setSerialno(_protocol.getSerialNo());
    
    pFWS(workInfo, handler);
    

    workInfo.setNoBattery(false);
    
    return true;
  }
  
  private void pFWS(WorkInfo workInfo, IComUSBHandler handler)
  {
    String pFWS = handler.excuteCommand("FWS", true);
    if (isEmpty(pFWS)) {
      return;
    }
    String[] ws = pFWS.split(",");
    
    warnsHandler(ws[1], "2001", workInfo);
    warnsHandler(ws[2], "2002", workInfo);
    warnsHandler(ws[3], "2003", workInfo);
    warnsHandler(ws[4], "2004", workInfo);
    
    warnsHandler(ws[5], "2005", workInfo);
    warnsHandler(ws[6], "2006", workInfo);
    warnsHandler(ws[7], "2007", workInfo);
    warnsHandler(ws[8], "2008", workInfo);
    
    warnsHandler(ws[9], "2009", workInfo);
    warnsHandler(ws[10], "2010", workInfo);
    warnsHandler(ws[11], "2011", workInfo);
    warnsHandler(ws[12], "2012", workInfo);
    
    warnsHandler(ws[13], "2013", workInfo);
    warnsHandler(ws[14], "2014", workInfo);
    warnsHandler(ws[15], "2015", workInfo);
    warnsHandler(ws[16], "2016", workInfo);
    
    workInfo.setLineLoss(ws[1].equals("1"));
    workInfo.setOverLoad(ws[8].equals("1"));
    setFaults(workInfo, ws[0]);
  }
  
  private void setFaults(WorkInfo workInfo, String faultCode)
  {
    if (VolUtil.parseInt(faultCode) > 0) {
      workInfo.setFault(true);
      DataBeforeFault data = new DataBeforeFault();
      data.setProdid(workInfo.getProdid());
      data.setSerialno(workInfo.getSerialno());
      data.setTrandate(workInfo.getCurrentTime());
      data.setWorkMode(workInfo.getWorkMode());
      data.setGridVoltage(workInfo.getGridCurrentR());
      data.setGridFrequency(workInfo.getGridFrequency());
      data.setAcoutputvoltager(workInfo.getAcOutputVoltageR());
      data.setAcoutputfrequency(workInfo.getAcOutputFrequency());
      data.setAcoutputapperentpowerr(workInfo.getAcOutputActivePowerR());
      data.setAcoutputactivepowerr(workInfo.getAcOutputActivePowerR());
      data.setBatteryVoltage(workInfo.getPBatteryVoltage());
      data.setBatteryDischargeCurrent(workInfo.getDisChargingCurrent());
      data.setBatteryChargingCurrent(workInfo.getChargingCurrent());
      data.setBatteryCapacity(workInfo.getBatteryCapacity());
      data.setPvinputpower1(workInfo.getPvInputPower1());
      data.setPvinputpower2(workInfo.getPvInputPower1());
      data.setPvinputvoltage1(workInfo.getPvInputVoltage1());
      data.setPvinputvoltage2(workInfo.getPvInputVoltage2());
      faultsHandler(faultCode, workInfo, data);
    }
  }
  

  private void pPGSn(int parallelKey, AbstractProcessor processor, IComUSBHandler handler)
  {
    String pPGSn = handler.excuteCommand("PGS" + parallelKey, true);
    if (isEmpty(pPGSn)) {
      return;
    }
    
    String[] pgs = pPGSn.split(",");
    String workMode = (String)workModeType.get("0" + pgs[1]);
    String faultCode = pgs[2];
    double gridVoltage = VolUtil.parseDouble(pgs[3]) / 10.0D;
    double gridFrequency = VolUtil.parseDouble(pgs[4]) / 10.0D;
    double acoutputvoltage = VolUtil.parseDouble(pgs[5]) / 10.0D;
    double acoutputfrequency = VolUtil.parseDouble(pgs[6]) / 10.0D;
    int acoutputapperentpower = VolUtil.parseInt(pgs[7]);
    int acoutputactivepower = VolUtil.parseInt(pgs[8]);
    int totalAcoutputapperentpower = VolUtil.parseInt(pgs[9]);
    int totalAcoutputactivepower = VolUtil.parseInt(pgs[10]);
    int outputLoadPercent = VolUtil.parseInt(pgs[11]);
    int totalOutputLoadPercent = VolUtil.parseInt(pgs[12]);
    double batteryVoltage = VolUtil.parseDouble(pgs[13]) / 10.0D;
    double batteryDischargeCurrent = VolUtil.parseDouble(pgs[14]);
    double batteryChargingCurrent = VolUtil.parseDouble(pgs[15]);
    double totalBatteryChargingCurrent = VolUtil.parseDouble(pgs[16]);
    int batteryCapacity = VolUtil.parseInt(pgs[17]);
    int pvinputpower1 = VolUtil.parseInt(pgs[18]);
    int pvinputpower2 = VolUtil.parseInt(pgs[19]);
    double pvinputvoltage1 = VolUtil.parseDouble(pgs[20]) / 10.0D;
    double pvinputvoltage2 = VolUtil.parseDouble(pgs[21]) / 10.0D;
    String pv1WorkStatus = pgs[22];
    String pv2WorkStatus = pgs[23];
    String loadConnection = pgs[24];
    String batteryStatus = pgs[25];
    String invDirection = pgs[26];
    String lineDirection = pgs[27];
    
    Calendar calendar = Calendar.getInstance();
    
    WorkInfo workInfo = (WorkInfo)processor.getBeanBag().getBean("workinfo");
    workInfo.setProdid(_protocol.getProtocolID());
    workInfo.setSerialno(processor.getSerialNo());
    workInfo.setCurrentTime(calendar.getTime());
    
    workInfo.setWorkMode(workMode);
    workInfo.setGridVoltageR(gridVoltage);
    workInfo.setGridFrequency(gridFrequency);
    workInfo.setAcOutputVoltageR(acoutputvoltage);
    workInfo.setAcOutputFrequency(acoutputfrequency);
    workInfo.setAcOutputApperentPowerR(acoutputapperentpower);
    workInfo.setAcOutputActivePowerR(acoutputactivepower);
    workInfo.setTotalACOutputApparentPower(totalAcoutputapperentpower);
    workInfo.setTotalACOutputActivePower(totalAcoutputactivepower);
    workInfo.setOutputLoadPercent(outputLoadPercent);
    workInfo.setTotalOutputLoadPercent(totalOutputLoadPercent);
    
    workInfo.setBatteryVoltage(batteryVoltage);
    workInfo.setDisChargingCurrent(batteryDischargeCurrent);
    workInfo.setChargingCurrent(batteryChargingCurrent);
    workInfo.setTotalBatteryChargingCurrent(totalBatteryChargingCurrent);
    workInfo.setBatteryCapacity(batteryCapacity);
    
    workInfo.setPvInputPower1(pvinputpower1);
    workInfo.setPvInputPower2(pvinputpower2);
    workInfo.setPvInputVoltage1(pvinputvoltage1);
    workInfo.setPvInputVoltage2(pvinputvoltage2);
    
    workInfo.setPv1WorkStatus(pv1WorkStatus);
    workInfo.setPv2WorkStatus(pv2WorkStatus);
    

    if ((workInfo.getPv1WorkStatus().equals("2")) || (workInfo.getPv2WorkStatus().equals("2"))) {
      workInfo.setPvLoss(false);
    } else {
      workInfo.setPvLoss(true);
    }
    

    if (loadConnection.equals("0")) {
      workInfo.setHasLoad(false);
    } else {
      workInfo.setHasLoad(true);
    }
    


    WorkInfo hsWorkInfo = (WorkInfo)getBeanBag().getBean("workinfo");
    workInfo.setLineLoss(hsWorkInfo.isLineLoss());
    if (workInfo.isLineLoss()) {
      warnsHandler("1", "2001", workInfo);
    } else {
      warnsHandler("0", "2001", workInfo);
    }
    

    workInfo.setNoBattery(false);
    
    workInfo.setBatteryStatus(batteryStatus);
    workInfo.setInvDirection(invDirection);
    workInfo.setLineDirection(lineDirection);
    

    String qPRIn = handler.excuteCommand("PRI" + parallelKey, true);
    if (isEmpty(qPRIn))
    {
      return;
    }
    String[] prin = qPRIn.split(",");
    
    String chargerSourcePriority = (String)chargerSourceType.get(prin[3]);
    double maxChargingCurrent = VolUtil.parseDouble(prin[4]);
    double maxAcChargingCurrent = VolUtil.parseDouble(prin[5]);
    String outputModel = prin[6];
    
    processor.setOutputmode(VolUtil.parseInt(outputModel));
    ConfigData configdata = (ConfigData)processor.getBeanBag().getBean("configdata");
    if ((processor instanceof ParallSubProcessor)) {
      configdata.setSubOutputMode(processor.getSerialNo(), (String)outputModelType.get(prin[6]));
      configdata.setChargerSourcePriority(processor.getSerialNo(), chargerSourcePriority);
      configdata.setMaxChargingCurrent(processor.getSerialNo(), maxChargingCurrent);
      configdata.setMaxAcChargingCurrent(processor.getSerialNo(), maxAcChargingCurrent);
    } else {
      configdata.setOutputModel((String)outputModelType.get(prin[6]));
      configdata.setChargerSourcePriority(chargerSourcePriority);
      configdata.setMaxChargingCurrent(maxChargingCurrent);
      configdata.setMaxAcChargingCurrent(maxAcChargingCurrent);
    }
    setFaults(workInfo, faultCode);
  }
  

  public boolean queryCapability()
  {
    Capability capability = (Capability)getBeanBag().getBean("capability");
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    try {
      pFLAG(capability, handler);
    } catch (Exception e) {
      logger.error("queryCapability Func -->" + e.getMessage());
      return false;
    }
    
    return true;
  }
  
  private void pFLAG(Capability capability, IComUSBHandler handler)
  {
    String pFLAG = handler.excuteCommand("FLAG", true);
    if (isEmpty(pFLAG)) {
      return;
    }
    String[] flag = pFLAG.split(",");
    capability.setCapableA(flag[0].equals("1"));
    capability.setCapableB(flag[1].equals("1"));
    capability.setCapableC(flag[2].equals("1"));
    capability.setCapableD(flag[3].equals("1"));
    capability.setCapableE(flag[4].equals("1"));
    capability.setCapableF(flag[5].equals("1"));
    capability.setCapableG(flag[6].equals("1"));
    capability.setCapableH(flag[7].equals("1"));
  }
  
  public boolean queryConfigData()
  {
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    try {
      pPIRI(handler, "qryConfigData");
      pMCHGCR(handler);
      pMUCHGCR(handler);
      pACCT(handler);
      pACLT(handler);
    } catch (Exception e) {
      logger.error("queryConfigData Func -->" + e.getMessage());
      return false;
    }
    return true;
  }
  
  private void pMCHGCR(IComUSBHandler handler)
  {
    ConfigData configdata = (ConfigData)getBeanBag().getBean("configdata");
    String pMCHGCR = handler.excuteCommand("MCHGCR", true);
    if (isEmpty(pMCHGCR)) {
      return;
    }
    String[] mchgcr = pMCHGCR.split(",");
    for (int i = 0; i < mchgcr.length; i++) {
      mchgcr[i] = VolUtil.parseDouble(mchgcr[i]);
    }
    configdata.setMaxChargingCurrentComBox(mchgcr);
  }
  
  private void pMUCHGCR(IComUSBHandler handler)
  {
    ConfigData configdata = (ConfigData)getBeanBag().getBean("configdata");
    String pMUCHGCR = handler.excuteCommand("MUCHGCR", true);
    if (isEmpty(pMUCHGCR)) {
      return;
    }
    String[] muchgcr = pMUCHGCR.split(",");
    for (int i = 0; i < muchgcr.length; i++) {
      muchgcr[i] = VolUtil.parseDouble(muchgcr[i]);
    }
    configdata.setMaxAcChargingCurrentCombox(muchgcr);
  }
  
  private void pACCT(IComUSBHandler handler)
  {
    ConfigData configdata = (ConfigData)getBeanBag().getBean("configdata");
    String pACCT = handler.excuteCommand("ACCT", true);
    if (isEmpty(pACCT)) {
      return;
    }
    String[] acct = pACCT.split(",");
    configdata.setAcChargeStarttime(acct[0]);
    configdata.setAcChargeEndtime(acct[1]);
  }
  
  private void pACLT(IComUSBHandler handler)
  {
    ConfigData configdata = (ConfigData)getBeanBag().getBean("configdata");
    String pACLT = handler.excuteCommand("ACLT", true);
    if (isEmpty(pACLT)) {
      return;
    }
    String[] aclt = pACLT.split(",");
    configdata.setAcoutputStarttime(aclt[0]);
    configdata.setAcoutputEndtime(aclt[1]);
  }
  

  public boolean queryDefaultData()
  {
    DefaultData defaultData = (DefaultData)getBeanBag().getBean("defaultdata");
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    try {
      pDI(defaultData, handler);
    } catch (Exception e) {
      logger.error("queryDefaultData Func -->" + e.getMessage());
      return false;
    }
    return true;
  }
  
  private void pDI(DefaultData defaultData, IComUSBHandler handler)
  {
    String pDI = handler.excuteCommand("DI", true);
    if (isEmpty(pDI)) {
      return;
    }
    String[] di = pDI.split(",");
    double acOutputVoltage = VolUtil.parseDouble(di[0]) / 10.0D;
    double acOutputFrequency = VolUtil.parseDouble(di[1]) / 10.0D;
    String acInputVoltageRange = (String)inputVoltageType.get(di[2]);
    double batteryUnderVoltage = VolUtil.parseDouble(di[3]) / 10.0D;
    double batteryFloatVoltage = VolUtil.parseDouble(di[4]) / 10.0D;
    double batteryBulkVoltage = VolUtil.parseDouble(di[5]) / 10.0D;
    double batteryRechargeVoltage = VolUtil.parseDouble(di[6]) / 10.0D;
    double batteryRedischargeVoltage = VolUtil.parseDouble(di[7]) / 10.0D;
    double maxChargingCurrent = VolUtil.parseDouble(di[8]);
    double maxACChargingCurrent = VolUtil.parseDouble(di[9]);
    String batType = (String)batteryType.get(di[10]);
    String outputSourcePriority = (String)outputSourceType.get(di[11]);
    String chargerSourcePriority = (String)chargerSourceType.get(di[12]);
    String solarPowerPriority = (String)solarPriorityType.get(di[13]);
    String mchType = (String)machineType.get(di[14]);
    String outputModel = (String)outputModelType.get(di[15]);
    
    defaultData.setAcOutputVoltage(acOutputVoltage);
    defaultData.setAcOutputFrequency(acOutputFrequency);
    defaultData.setAcInputVoltageRange(acInputVoltageRange);
    defaultData.setBatteryUnderVoltage(batteryUnderVoltage);
    defaultData.setBatteryFloatVoltage(batteryFloatVoltage);
    defaultData.setBatteryBulkVoltage(batteryBulkVoltage);
    defaultData.setBatteryRechargeVoltage(batteryRechargeVoltage);
    defaultData.setBatteryRedischargeVoltage(batteryRedischargeVoltage);
    defaultData.setMaxChargingCurrent(maxChargingCurrent);
    defaultData.setMaxACChargingCurrent(maxACChargingCurrent);
    defaultData.setBatteryType(batType);
    defaultData.setOutputSourcePriority(outputSourcePriority);
    defaultData.setChargerSourcePriority(chargerSourcePriority);
    defaultData.setSolarPowerPriority(solarPowerPriority);
    defaultData.setMachineType(mchType);
    defaultData.setOutputModel(outputModel);
    
    defaultData.setCapableA((String)enable.get(di[16]));
    defaultData.setCapableD((String)enable.get(di[17]));
    defaultData.setCapableE((String)enable.get(di[18]));
    defaultData.setCapableF((String)enable.get(di[19]));
    defaultData.setCapableG((String)enable.get(di[20]));
    defaultData.setCapableH((String)enable.get(di[21]));
    defaultData.setCapableB((String)enable.get(di[22]));
    defaultData.setCapableC((String)enable.get(di[23]));
  }
  
  public boolean queryMachineInfo()
  {
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    if (handler == null) {
      return false;
    }
    pVFW(handler);
    pPIRI(handler, "qryMachineInfo");
    
    return true;
  }
  
  private void pVFW(IComUSBHandler handler)
  {
    MachineInfo machineInfo = (MachineInfo)getBeanBag().getBean("machineinfo");
    String pVFW = handler.excuteCommand("VFW", true);
    if (isEmpty(pVFW)) {
      return;
    }
    String[] version = pVFW.split(",");
    machineInfo.setMainFirmwareVersion(version[0]);
    machineInfo.setSlaveFirmwareVersion1(version[1]);
    machineInfo.setSlaveFirmwareVersion2(version[2]);
  }
  

  private void pPIRI(IComUSBHandler handler, String action)
  {
    String pPIRI = handler.excuteCommand("PIRI", true);
    if (isEmpty(pPIRI)) {
      return;
    }
    String[] piri = pPIRI.split(",");
    double acInputRatingVoltage = VolUtil.parseDouble(piri[0]) / 10.0D;
    double acInputRatingCurrent = VolUtil.parseDouble(piri[1]) / 10.0D;
    double acOutputRatingVoltage = VolUtil.parseDouble(piri[2]) / 10.0D;
    double acOutputRatingFrequency = VolUtil.parseDouble(piri[3]) / 10.0D;
    double acOutputRatingCurrent = VolUtil.parseDouble(piri[4]) / 10.0D;
    int acOutputRatingApparentPower = VolUtil.parseInt(piri[5]);
    int acOutputRatingActivePower = VolUtil.parseInt(piri[6]);
    double batteryRatingVoltage = VolUtil.parseDouble(piri[7]) / 10.0D;
    
    double batteryRechargeVoltage = VolUtil.parseDouble(piri[8]) / 10.0D;
    double batteryRedischargeVoltage = VolUtil.parseDouble(piri[9]) / 10.0D;
    double batteryUnderVoltage = VolUtil.parseDouble(piri[10]) / 10.0D;
    double batteryBulkVoltage = VolUtil.parseDouble(piri[11]) / 10.0D;
    double batteryFloatVoltage = VolUtil.parseDouble(piri[12]) / 10.0D;
    String batType = (String)batteryType.get(piri[13]);
    double maxACChargingCurrent = VolUtil.parseDouble(piri[14]);
    double maxChargingCurrent = VolUtil.parseDouble(piri[15]);
    String inputVoltageRange = (String)inputVoltageType.get(piri[16]);
    String outputSourcePriority = (String)outputSourceType.get(piri[17]);
    String chargerSourcePriority = (String)chargerSourceType.get(piri[18]);
    int maxParallelNum = VolUtil.parseInt(piri[19]);
    String mchType = (String)machineType.get(piri[20]);
    String topology = (String)topologyType.get(piri[21]);
    String outputModelSetting = (String)outputModelType.get(piri[22]);
    String solarPowerPriority = (String)solarPriorityType.get(piri[23]);
    int mpptTrackNumber = VolUtil.parseInt(piri[24]);
    
    String regulationsState = "India";
    try {
      regulationsState = (String)regulationType.get(piri[25]);
    }
    catch (Exception localException) {}
    int outputmode = VolUtil.parseInt(piri[22]);
    _protocol.setOutputMode(outputmode);
    
    _outputmode = outputmode;
    if (outputmode != 0) {
      _paralleltype = 1;
      _parallelnum = VolUtil.parseInt(piri[19]);
    } else {
      _paralleltype = 0;
    }
    
    if (action.equals("qryMachineInfo")) {
      MachineInfo machineInfo = (MachineInfo)getBeanBag().getBean("machineinfo");
      machineInfo.setAcInputRatingVoltage(acInputRatingVoltage);
      machineInfo.setAcInputRatingCurrent(acInputRatingCurrent);
      machineInfo.setAcOutputRatingVoltage(acOutputRatingVoltage);
      machineInfo.setAcOutputRatingFrequency(acOutputRatingFrequency);
      machineInfo.setAcOutputRatingCurrent(acOutputRatingCurrent);
      machineInfo.setAcOutputRatingApparentPower(acOutputRatingApparentPower);
      machineInfo.setAcOutputRatingActivePower(acOutputRatingActivePower);
      machineInfo.setBatteryRatingVoltage(batteryRatingVoltage);
      machineInfo.setMachineType(mchType);
      machineInfo.setTopology(topology);
      
      machineInfo.setBatteryRechargeVoltage(batteryRechargeVoltage);
      machineInfo.setBatteryRedischargeVoltage(batteryRedischargeVoltage);
      
      machineInfo.setBatteryUnderVoltage(batteryUnderVoltage);
      machineInfo.setBatteryBulkVoltage(batteryBulkVoltage);
      machineInfo.setBatteryFloatVoltage(batteryFloatVoltage);
      
      machineInfo.setBatteryType(batType);
      machineInfo.setMaxACChargingCurrent(maxACChargingCurrent);
      machineInfo.setMaxChargingCurrent(maxChargingCurrent);
      machineInfo.setInputVoltageRange(inputVoltageRange);
      machineInfo.setOutputSourcePriority(outputSourcePriority);
      machineInfo.setChargerSourcePriority(chargerSourcePriority);
      machineInfo.setMaxParallelNum(maxParallelNum);
      machineInfo.setOutputModel(outputModelSetting);
      machineInfo.setSolarPowerPriority(solarPowerPriority);
      machineInfo.setMpptTrackNumber(mpptTrackNumber);
    }
    else if (action.equals("qryConfigData")) {
      ConfigData configdata = (ConfigData)getBeanBag().getBean("configdata");
      Capability capability = (Capability)getBeanBag().getBean("capability");
      configdata.setAcOutputRatingVoltage(acOutputRatingVoltage);
      configdata.setAcOutputRatingFrequency(acOutputRatingFrequency);
      configdata.setBatteryRatingVoltage(batteryRatingVoltage);
      
      configdata.setBatteryRechargeVoltage(batteryRechargeVoltage);
      configdata.setBatteryRedischargeVoltage(batteryRedischargeVoltage);
      
      configdata.setBatteryUnderVoltage(batteryUnderVoltage);
      configdata.setBatteryBulkVoltage(batteryBulkVoltage);
      configdata.setBatteryFloatVoltage(batteryFloatVoltage);
      

      if (configdata.getBatteryRatingVoltage() > 40.0D) {
        configdata.setMinBatteryUnderVoltage(40.0D);
        configdata.setMaxBatteryUnderVoltage(48.0D);
        configdata.setMinBatteryBulkVoltage(48.0D);
        configdata.setMaxBatteryBulkVoltage(58.4D);
        configdata.setMinBatteryFloatVoltage(48.0D);
        configdata.setMaxBatteryFloatVoltage(58.4D);
      } else if (configdata.getBatteryRatingVoltage() > 20.0D)
      {
        configdata.setMinBatteryUnderVoltage(20.4D);
        configdata.setMaxBatteryUnderVoltage(24.0D);
        configdata.setMinBatteryBulkVoltage(24.0D);
        configdata.setMaxBatteryBulkVoltage(29.2D);
        configdata.setMinBatteryFloatVoltage(24.0D);
        configdata.setMaxBatteryFloatVoltage(29.2D);
      }
      else
      {
        configdata.setMinBatteryUnderVoltage(10.2D);
        configdata.setMaxBatteryUnderVoltage(12.0D);
        configdata.setMinBatteryBulkVoltage(12.0D);
        configdata.setMaxBatteryBulkVoltage(14.6D);
        configdata.setMinBatteryFloatVoltage(12.0D);
        configdata.setMaxBatteryFloatVoltage(14.6D);
      }
      configdata.setBatteryType(batType);
      configdata.setMaxAcChargingCurrent(maxACChargingCurrent);
      configdata.setMaxChargingCurrent(maxChargingCurrent);
      configdata.setInputVoltageRange(inputVoltageRange);
      configdata.setOutputSourcePriority(outputSourcePriority);
      configdata.setChargerSourcePriority(chargerSourcePriority);
      configdata.setOutputModel(outputModelSetting);
      configdata.setSolarPowerPriority(solarPowerPriority);
      
      capability.setCapableI(piri[20].equals("1"));
      
      configdata.setRegulationsState(regulationsState);
    }
  }
  


  public Calendar queryCurrentTime()
  {
    Calendar cal = null;
    IComUSBHandler handler = (IComUSBHandler)getHandler();
    String pT = handler.excuteCommand("T", true);
    if (isEmpty(pT)) {
      return cal;
    }
    if (pT.trim().length() != 14)
    {
      return cal;
    }
    Date date = DateUtils.parseDate(pT, "yyyyMMddHHmmss");
    if (date != null)
    {
      cal = Calendar.getInstance();
      cal.setTime(date);
    }
    return cal;
  }
  


  public void querySelfTestResult() {}
  

  public boolean supportSelfTest()
  {
    return false;
  }
  

  public boolean queryDeviceModel()
  {
    return false;
  }
  


  public void queryEnergyBeginDate() {}
  

  public double queryEnergyDay(Calendar trandate)
    throws Exception
  {
    synchronized (query_day)
    {
      double energyDay = 0.0D;
      IComUSBHandler handler = (IComUSBHandler)getHandler();
      if (handler == null)
      {
        throw new Exception("queryEnergyDay handler is null...");
      }
      Calendar calendar = (Calendar)trandate.clone();
      String value = DateUtils.getFormatDate(calendar.getTime(), "yyyyMMdd");
      String qedStr = handler.excuteCommand("ED" + value, true);
      if ((qedStr != null) && (!"".equals(qedStr)) && (!qedStr.equals("(NAK")))
      {
        energyDay = parseDoubleV(qedStr);
      }
      else
      {
        throw new Exception("query day energy error");
      }
      return energyDay;
    }
  }
  
  public double queryEnergyHour(Calendar trandate, int hour) throws Exception
  {
    return 0.0D;
  }
  
  public double queryEnergyMonth(int year, int month)
    throws Exception
  {
    synchronized (query_month)
    {
      double energyMonth = 0.0D;
      IComUSBHandler handler = (IComUSBHandler)getHandler();
      if (handler == null)
      {
        throw new Exception("queryEnergyMonth handler is null");
      }
      String monthStr = month;
      String value = year + monthStr;
      String qemStr = handler.excuteCommand("EM" + value, true);
      if ((qemStr != null) && (!"".equals(qemStr)) && (!qemStr.equals("(NAK")))
      {
        energyMonth = parseDoubleV(qemStr);
      }
      else
      {
        throw new Exception("query month energy error");
      }
      return energyMonth;
    }
  }
  
  public double queryEnergyYear(int year)
    throws Exception
  {
    synchronized (query_year)
    {
      double energyYear = 0.0D;
      IComUSBHandler handler = (IComUSBHandler)getHandler();
      if (handler == null)
      {
        throw new Exception("queryEnergyYear handler is null");
      }
      String value = year;
      String qeyStr = handler.excuteCommand("EY" + value, true);
      if ((qeyStr != null) && (!"".equals(qeyStr)) && (!qeyStr.equals("(NAK")))
      {
        energyYear = parseDoubleV(qeyStr);
      }
      else
      {
        throw new Exception("query year energy error");
      }
      return energyYear;
    }
  }
  

  public double queryEnergyTotal()
  {
    synchronized (query_tatal)
    {
      double energyTotal = 0.0D;
      IComUSBHandler handler = (IComUSBHandler)getHandler();
      if (handler == null)
      {
        return energyTotal;
      }
      String qetStr = handler.excuteCommand("ET", true);
      if (!isEmpty(qetStr))
      {
        energyTotal = VolUtil.parseDouble(qetStr);
      }
      else
      {
        energyTotal = 0.0D;
      }
      return energyTotal;
    }
  }
  
  public float queryFWVersion()
  {
    return 0.0F;
  }
}
