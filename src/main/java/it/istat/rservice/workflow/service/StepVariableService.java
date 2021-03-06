/**
 * Copyright 2019 ISTAT
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations under
 * the Licence.
 *
 * @author Francesco Amato <framato @ istat.it>
 * @author Mauro Bruno <mbruno @ istat.it>
 * @author Paolo Francescangeli  <pafrance @ istat.it>
 * @author Renzo Iannacone <iannacone @ istat.it>
 * @author Stefano Macone <macone @ istat.it>
 * @version 1.0
 */
package it.istat.rservice.workflow.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.istat.rservice.app.domain.Elaborazione;
import it.istat.rservice.workflow.dao.StepVariableDao;
import it.istat.rservice.workflow.domain.SxRuoli;
import it.istat.rservice.workflow.domain.SxStepVariable;

@Service
public class StepVariableService {

    @Autowired
    StepVariableDao stepVariableDao;

    public List<SxStepVariable> findBStepByIdProcess(Long idelab, Long idstep) {
        return stepVariableDao.findByElaborazione(new Elaborazione(idelab));
    }

    public SxStepVariable findById(Long idstep) {
        return stepVariableDao.findById(idstep);
    }

    public List<SxStepVariable> findBySxRuoli(SxRuoli ruolo) {
        return stepVariableDao.findBySxRuoli(ruolo);
    }

    public void removeStepVarById(Long idstep) {
        stepVariableDao.delete(idstep);
    }

    public void updateStepVar(SxStepVariable stepV) {
        stepVariableDao.save(stepV);
    }
}
