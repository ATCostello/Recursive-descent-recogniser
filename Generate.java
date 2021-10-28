import java.util.ArrayList;
import java.util.List;

/**
 *
 * Generate methods for 312 exercise.
 *
 * @Author: Alfred Costello
 *
 *
 **/

public class Generate extends AbstractGenerate
{

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        // TODO Auto-generated method stub
        System.out.println("rggERROR Error encountered with token " + token + " with error message " + explanatoryMessage);
    }

} // end of class Generate