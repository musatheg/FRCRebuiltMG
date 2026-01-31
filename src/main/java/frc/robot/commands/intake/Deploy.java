package frc.robot.commands.intake;
import frc.robot.subsystems.Intake;
import frc.robot.testingdashboard.Command;

public class Deploy extends Command {
    private Intake m_Intake;
    
    public Deploy() {
        super(Intake.getInstance(), "Deploy", "Deploy");
        m_Intake = Intake.getInstance();
    }
    
}
