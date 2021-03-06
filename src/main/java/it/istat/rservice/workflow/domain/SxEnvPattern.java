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
package it.istat.rservice.workflow.domain;

import java.io.Serializable;
import javax.persistence.*;

 
import lombok.Data;
 



@Data
@Entity
@Table(name = "SX_ENV_PATTERN")
 
@NamedQuery(name = "SxEnvPattern.findAll", query = "SELECT s FROM SxEnvPattern s")
public class SxEnvPattern implements Serializable {

    private static final long serialVersionUID = 1L;

    
    
 // bi-directional many-to-one association to SxRuoli
    @Id
    @ManyToOne
    @JoinColumn(name = "TYPE_IO")
    private SxTipoIO tipoIO;

    // bi-directional many-to-one association to SxRuoli
    @Id
    @ManyToOne
    @JoinColumn(name = "ENV")
    private SxAppServiceEnv  sxAppServiceEnv;

    // bi-directional many-to-one association to SxStepInstance
    @Id
    @ManyToOne
    @JoinColumn(name = "ISTANCE")
    private SxStepInstance sxStepInstance;
    
    public SxEnvPattern() {
    }

}
