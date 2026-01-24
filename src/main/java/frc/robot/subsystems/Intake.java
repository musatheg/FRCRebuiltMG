package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;

import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.testingdashboard.TDNumber;
import frc.robot.utils.Configuration;
import frc.robot.testingdashboard.SubsystemBase;



public class Intake extends SubsystemBase{

    SparkFlex m_ISparkFlex;
    SparkFlexConfig m_SparkFlexConfig;

    TDNumber td_currentOutput;

    private static Intake m_Intake = null;

    private Intake(){
        super("Intake");
        if (cfgBool("intakeEnabled") == true){
            m_ISparkFlex = new SparkFlex(cfgInt("intakeRollerCanId"), null);
            m_SparkFlexConfig = new SparkFlexConfig();
            m_SparkFlexConfig
                .idleMode(IdleMode.kCoast)
                .smartCurrentLimit(cfgInt("intakeRollerStallLimit"), cfgInt("intakeRollerFreeLimit"));

            //SparkFlexConfig m_SparkFlexConfig2 = new SparkFlexConfig();
            //m_SparkFlexConfig2.follow(Constants.IntakeConstants.kIntakeID, RobotMap.I_OPPOSITE);

            m_ISparkFlex.configure(m_SparkFlexConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            td_currentOutput = new TDNumber(this, "Intake", "Ground Intake");
                
        }
    }

    double apple = Configuration.getInstance().getDouble("Drive", "testDouble");
    double fhuwi = cfgDbl("testDouble"); //how to get variable from config

    public static Intake getInstance() {
        if (m_Intake == null) {
            m_Intake = new Intake();
        }
        return m_Intake;
    }
    public void spinIn(double speed) {
        if (m_ISparkFlex != null) {
            m_ISparkFlex.set(speed);
        }
    }
    public void spinOut(double speed) {
        if (m_ISparkFlex != null) {
            m_ISparkFlex.set(-1 * speed);
        }
    }
    public void stop() {
        if (m_ISparkFlex != null) {
            m_ISparkFlex.set(-0);
        }
    }
    
    @Override
    public void periodic() {
        if (cfgBool("intakeEnabled") == true) {
            td_currentOutput.set(m_ISparkFlex.getOutputCurrent());
        }
        super.periodic();
    }
    
}
