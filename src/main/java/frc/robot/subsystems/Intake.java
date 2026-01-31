package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
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

    SparkFlex m_rollerMotor;
    SparkFlexConfig m_rollerConfig;

    SparkFlex m_deployMotor;
    SparkFlexConfig m_deployConfig;

    TDNumber m_TDcurrentOutput;
    TDNumber m_TDdeployTargetPosition;
    TDNumber m_TDdeployP;
    TDNumber m_TDdeployI;
    TDNumber m_TDdeployD;
    double m_deployP;
    double m_deployI;
    double m_deployD;

    private static Intake m_Intake = null;

    private Intake() {
        super("Intake");
        if (cfgBool("intakeEnabled") == true){
            m_rollerMotor = new SparkFlex(cfgInt("rollerCanId"), MotorType.kBrushless);
            m_rollerConfig = new SparkFlexConfig();
            m_rollerConfig
                .idleMode(IdleMode.kCoast)
                .smartCurrentLimit(cfgInt("rollerStallLimit"), cfgInt("rollerFreeLimit"));

            

            //SparkFlexConfig m_SparkFlexConfig2 = new SparkFlexConfig();
            //m_SparkFlexConfig2.follow(Constants.IntakeConstants.kIntakeID, RobotMap.I_OPPOSITE);

            m_rollerMotor.configure(m_rollerConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            m_deployMotor = new SparkFlex(cfgInt("deployCanId"), MotorType.kBrushless); //TODO: Update CanID later
            m_deployConfig = new SparkFlexConfig();
            m_deployConfig
                .idleMode(IdleMode.kBrake)
                .absoluteEncoder.positionConversionFactor(cfgDbl("deployPositionFactor"));

            m_TDdeployP = new TDNumber(this, "Deploy", "P");
            m_TDdeployI = new TDNumber(this, "Deploy", "I");
            m_TDdeployD = new TDNumber(this, "Deploy", "D");
            
            m_TDdeployP.set(cfgDbl("deployP"));
            m_TDdeployI.set(cfgDbl("deployI"));
            m_TDdeployD.set(cfgDbl("deployD"));

            m_deployP = m_TDdeployP.get();
            m_deployI = m_TDdeployI.get();
            m_deployD = m_TDdeployD.get();
            
            m_deployConfig.closedLoop.pid(m_deployP, m_deployI, m_deployD);

            //SparkFlexConfig m_SparkFlexConfig2 = new SparkFlexConfig();
            //m_SparkFlexConfig2.follow(Constants.IntakeConstants.kIntakeID, RobotMap.I_OPPOSITE);

            m_deployMotor.configure(m_deployConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

            m_TDcurrentOutput = new TDNumber(this, "Roller", "Ground Intake");
            m_TDdeployTargetPosition = new TDNumber(this, "Deploy", "Target Position");
        }
    }

    public static Intake getInstance() {
        if (m_Intake == null) {
            m_Intake = new Intake();
        }
        return m_Intake;
    }
    public void spinIn(double speed) {
        if (m_rollerMotor != null) {
            m_rollerMotor.set(speed);
        }
    }
    public void spinOut(double speed) {
        if (m_rollerMotor != null) {
            m_rollerMotor.set(-1 * speed);
        }
    }
    public void stop() {
        if (m_rollerMotor != null) {
            m_rollerMotor.set(-0);
        }
    }

    public void deploy() {
        m_TDdeployTargetPosition.set(cfgDbl("deployPosition"));
    }

    public void retract() {
        m_TDdeployTargetPosition.set(cfgDbl("retractPosition"));
    }
    
    @Override
    public void periodic() {
        if (cfgBool("intakeEnabled") == true) {
            
            if (cfgBool("tuneDeployPID") && (m_deployP != m_TDdeployP.get() || m_deployI != m_TDdeployI.get() || m_deployD != m_TDdeployD.get())) {
                m_deployP = m_TDdeployP.get();
                m_deployI = m_TDdeployI.get();
                m_deployD = m_TDdeployD.get();
                m_deployConfig.closedLoop.pid(m_deployP, m_deployI, m_deployD);
                m_deployMotor.configure(m_deployConfig,ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
            }
            
            m_deployMotor.getClosedLoopController().setSetpoint(m_TDdeployTargetPosition.get(), ControlType.kPosition);
            m_TDcurrentOutput.set(m_rollerMotor.getOutputCurrent());
            
            
        }
        super.periodic();
    }
    
}
