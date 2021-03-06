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
package it.istat.rservice.app.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import it.istat.rservice.app.bean.InputFormBean;
import it.istat.rservice.workflow.domain.SxBusinessProcess;
import it.istat.rservice.workflow.domain.SxRuoli;
import it.istat.rservice.workflow.domain.SxStepVariable;
import it.istat.rservice.workflow.domain.SxTipoVar;

public class Utility {

    public static String getLocalDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Rome"), Locale.ITALY);
        Date today = calendar.getTime();
        String dataOutput = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");
            dataOutput = simpleDateFormat.format(today);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataOutput;
    }

    public static String buildQueryFilter(Long idelaborazione, HashMap<String, String> paramsFilter) {

        String paramQuery = " ";

        if (paramsFilter != null) {
            for (String key : paramsFilter.keySet()) {
                String value = paramsFilter.get(key);
                paramQuery += " and t.r in( select f.r from SX_WORKSET si, SX_STEP_VARIABLE ssv,json_table(si.valori, '$.valori[*]'  columns "
                        + "(  idx FOR ORDINALITY,r integer path '$.r', v varchar2 path '$.v') ) f "
                        + " where  ssv.elaborazione=" + idelaborazione + " and ssv.var=si.id  and si.nome='" + key
                        + "' and f.v='" + value + "') ";
            }
        }
        return paramQuery;
    }

    public static HashMap<String, String> recuperaParametri(HashMap<String, String> params,
            Map<String, String[]> parameterMap) {
        // HashMap per l'invio in procedura
        HashMap<String, String> paramsToUse = new HashMap<String, String>();
        String param = "";

        for (String key : params.keySet()) {

            if (parameterMap.get("param_" + key) != null) {
                param = parameterMap.get("param_" + key)[0];
                paramsToUse.put(key, param);
            }
        }
        return paramsToUse;
    }

    // Ritorna un HashMap che mappa in modo dinamico i tipi dell'header provenienti
    // dal form con gli indici dell'header che abbiamo dal file
    public static HashMap<Integer, List<Integer>> mappaHeaderValues(Map<String, String[]> parameterMap,
            ArrayList<String> valoriHeader, int size) {
        // Mappa in modo dinamico i tipi dell'header provenienti dal form con gli indici
        // dell'header che abbiamo dal file
        HashMap<Integer, List<Integer>> headerValues = new HashMap<Integer, List<Integer>>();
        List<List<Integer>> colonne = new ArrayList<List<Integer>>();

        for (int i = 0; i < size; i++) {
            List<Integer> list = new ArrayList<>();
            colonne.add(list);
        }
        // Ogni valore iesimo per noi è una colonna dell'intestazione
        for (int i = 0; i < valoriHeader.size(); i++) {

            String value = parameterMap.get("sel_" + i)[0];
            int intValue = Integer.parseInt(value);
            // Controlla i tipi e riempie l'hashmap con la relativa etichetta (ad es. chiave
            // "variabile core" -> lista variabili core)
            // TODO: I tipi vanno presi da un file di configurazione dei metadati,
            // attualmente sono in calce lato javascript e lato java: Skip:0, Id:1, Y:2,
            // X:3, Pred:4
            switch (intValue) {
                case 0:
                    colonne.get(0).add(i);
                    break;
                case 1:
                    colonne.get(1).add(i);
                    break;
                case 2:
                    colonne.get(2).add(i);
                    break;
                case 3:
                    colonne.get(3).add(i);
                    break;
                case 4:
                    colonne.get(4).add(i);
                    break;
            }

        }
        // Vado a riempire l'hashmap con gli array di interi in modo posizionale
        // rispetto all'header
        // Le chiavi saranno sempre quelle del file di configurazione dei tipi, per ora:
        // Skip:0, Id:1, Y:2, X:3, Pred:4
        for (int i = 0; i < size; i++) {
            headerValues.put(i, colonne.get(i));
        }
        return headerValues;
    }

    public static void stampaValoriHeader(HashMap<Integer, List<Integer>> headerValues, int size) {
        for (int i = 0; i < size; i++) {
            switch (i) {
                case 0:
                    Logger.getRootLogger().info("Colonne skippate: " + headerValues.get(0));
                    break;
                case 1:
                    Logger.getRootLogger().info("Colonne Id: " + headerValues.get(1));
                    break;
                case 2:
                    Logger.getRootLogger().info("Colonne Y: " + headerValues.get(2));
                    break;
                case 3:
                    Logger.getRootLogger().info("Colonne X: " + headerValues.get(3));
                    break;
                case 4:
                    Logger.getRootLogger().info("Colonne Pred: " + headerValues.get(4));
                    break;
            }
        }
    }

    public static HashMap<String, String> createParamsMap() {
        // TODO: da integrare con la gestione parametri da file e sintesi
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("model", "LN");
        params.put("lambda", "3");
        params.put("w", "0.0479");
        params.put("lambda.fix", "TRUE");
        params.put("w.fix", "TRUE");
        params.put("eps", "1e-7");
        params.put("max.iter", "500");
        params.put("t.outl", "0.5");
        params.put("graph", "TRUE");
        params.put("beta", "-0.152 1.215");
        params.put("sigma", "1.25");
        params.put("n.outlier", "0");
        params.put("is.conv", "");
        params.put("n.iter", "0");
        params.put("sing", "");
        params.put("bic.aic", "");
        params.put("wgt", "");
        params.put("tot", "");
        params.put("t.sel", "");
        return params;
    }

    public static HashMap<Integer, List<String>> creaListaTipi() {
        // HashMap tipi: TODO: da integrare con la gestione parametri da file e sintesi
        HashMap<Integer, List<String>> tipoDato = new HashMap<Integer, List<String>>();
        ArrayList<String> skip = new ArrayList<String>();
        skip.add(0, "Z");
        skip.add(1, "skip");

        List<String> identificativo = new ArrayList<String>();
        identificativo.add(0, "Id");
        identificativo.add(1, "identificativo");

        List<String> target = new ArrayList<String>();
        target.add(0, "Y");
        target.add(1, "target");

        List<String> covariata = new ArrayList<String>();
        covariata.add(0, "X");
        covariata.add(1, "covariata");

        List<String> predizione = new ArrayList<String>();
        predizione.add(0, "P");
        predizione.add(1, "predizione");

        List<String> outlier = new ArrayList<String>();
        outlier.add(0, "O");
        outlier.add(1, "outlier");

        List<String> peso = new ArrayList<String>();
        peso.add(0, "W");
        peso.add(1, "peso");

        List<String> errore = new ArrayList<String>();
        errore.add(0, "E");
        errore.add(1, "errore");

        List<String> ranking = new ArrayList<String>();
        ranking.add(0, "R");
        ranking.add(1, "ranking");

        List<String> output = new ArrayList<String>();
        output.add(0, "Out");
        output.add(1, "output");

        List<String> strato = new ArrayList<String>();
        strato.add(0, "S");
        strato.add(1, "strato");

        tipoDato.put(0, skip);
        tipoDato.put(1, identificativo);
        tipoDato.put(2, target);
        tipoDato.put(3, covariata);
        tipoDato.put(4, predizione);
        tipoDato.put(5, outlier);
        tipoDato.put(6, peso);
        tipoDato.put(7, errore);
        tipoDato.put(8, ranking);
        tipoDato.put(9, output);
        tipoDato.put(10, strato);
        return tipoDato;
    }

    public static HashMap<String, List<String>> recuperaVariabiliForm(InputFormBean form,
            HashMap<String, List<String>> headerValuesSelected) {
        headerValuesSelected = new HashMap<String, List<String>>();
        String identificativo = form.getIdentificativo();
        if (!identificativo.equals("")) {
            identificativo = identificativo.substring(0, identificativo.length() - 1);
        }
        String target = form.getTarget();
        if (!target.equals("")) {
            target = target.substring(0, target.length() - 1);
        }
        String covariata = form.getCovariata();
        if (!covariata.equals("")) {
            covariata = covariata.substring(0, covariata.length() - 1);
        }
        String predizione = form.getPredizioni();
        if (!predizione.equals("")) {
            predizione = predizione.substring(0, predizione.length() - 1);
        }
        String outlier = form.getOutlier();
        if (outlier == null) {
            outlier = "";
        }
        if (outlier != null && !outlier.equals("")) {
            outlier = outlier.substring(0, outlier.length() - 1);
        }
        String delim = ",";

        StringTokenizer st = new StringTokenizer(identificativo, delim);
        String chiaveId = "identificativo";

        ArrayList<String> valoriId = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            valoriId.add(value);
        }
        headerValuesSelected.put(chiaveId, valoriId);

        StringTokenizer st2 = new StringTokenizer(target, delim);
        String chiaveCorr = "target";
        ArrayList<String> valoriCorr = new ArrayList<String>();
        while (st2.hasMoreTokens()) {
            String value = st2.nextToken();
            valoriCorr.add(value);
        }
        headerValuesSelected.put(chiaveCorr, valoriCorr);

        StringTokenizer st3 = new StringTokenizer(covariata, delim);
        String chiaveCov = "covariata";
        ArrayList<String> valoriCov = new ArrayList<String>();
        while (st3.hasMoreTokens()) {
            String value = st3.nextToken();
            valoriCov.add(value);
        }
        headerValuesSelected.put(chiaveCov, valoriCov);

        StringTokenizer st4 = new StringTokenizer(predizione, delim);
        String chiavePred = "predizione";
        ArrayList<String> valoriPred = new ArrayList<String>();
        while (st4.hasMoreTokens()) {
            String value = st4.nextToken();
            valoriPred.add(value);
        }
        headerValuesSelected.put(chiavePred, valoriPred);

        StringTokenizer st5 = new StringTokenizer(outlier, delim);
        String chiaveOutl = "outlier";
        ArrayList<String> valoriOutl = new ArrayList<String>();
        while (st5.hasMoreTokens()) {
            String value = st5.nextToken();
            valoriOutl.add(value);
        }
        headerValuesSelected.put(chiaveOutl, valoriOutl);

        return headerValuesSelected;
    }

    /**
     * @param sxTipoVar
     * @param dataList
     * @return
     */
    public static HashMap<String, ArrayList<String>> getMapWorkSetValues(Map<String, SxStepVariable> dataMap,
            SxTipoVar sxTipoVar) {

        HashMap<String, ArrayList<String>> ret = new HashMap<>();

        for (Map.Entry<String, SxStepVariable> entry : dataMap.entrySet()) {
            String nomeW = entry.getKey();
            SxStepVariable sxStepVariable = entry.getValue();
            if (sxStepVariable.getSxWorkset().getSxTipoVar().getId().equals(sxTipoVar.getId())) {
                ret.put(nomeW, (ArrayList<String>) sxStepVariable.getSxWorkset().getValori());
            }
        }
        return ret;
    }

    public static Map<String, ArrayList<SxStepVariable>> getMapCodiceRuoloStepVariabili(List<SxStepVariable> dataList) {

        HashMap<String, ArrayList<SxStepVariable>> ret = new HashMap<>();
        for (SxStepVariable stepV : dataList) {
            String codR = stepV.getSxRuoli().getCod();
            ArrayList<SxStepVariable> lista = ret.get(codR);
            if (lista == null) {
                lista = new ArrayList<SxStepVariable>();
            }
            lista.add(stepV);
            ret.put(codR, lista);
        }
        return ret;
    }

    public static Map<String, SxStepVariable> getMapNameWorkSetStep(List<SxStepVariable> dataList) {

        HashMap<String, SxStepVariable> ret = new HashMap<>();
        for (SxStepVariable sxStepVariable : dataList) {

            ret.put(sxStepVariable.getSxWorkset().getNome(), sxStepVariable);
        }
        return ret;
    }

    /**
     * @param ruoliAll
     * @return
     */
    public static Map<String, SxRuoli> getMapRuoliByCod(List<SxRuoli> ruoliAll) {

        HashMap<String, SxRuoli> ret = new HashMap<>();
        for (SxRuoli sxRuoli : ruoliAll) {

            ret.put(sxRuoli.getCod(), sxRuoli);
        }
        return ret;
    }

    public static String combineList2String4R(List<String> lista) {
        String c = "";
        if (lista != null && !lista.isEmpty()) {
            c = "c(";
            for (String s : lista) {
                c += "'" + s + "',";
            }
            c = c.substring(0, c.length() - 1) + ")";
        }
        Logger.getRootLogger().debug("c4R: <" + c + ">");
        return c;
    }

    public static Map<Long, SxRuoli> getMapRuoliById(List<SxRuoli> ruoliAll) {
        HashMap<Long, SxRuoli> ret = new HashMap<>();
        for (SxRuoli sxRuoli : ruoliAll) {

            ret.put(sxRuoli.getId(), sxRuoli);
        }
        return ret;
    }

    /**
     * @param listaBp
     * @return
     */
    public static SxBusinessProcess getSxBusinessProcess(List<SxBusinessProcess> listaBp, Long idprocesso) {

        for (SxBusinessProcess bP : listaBp) {
            if (bP.getId().equals(idprocesso)) {
                return bP;
            }
        }
        return null;

    }

    public static void writeObjectToCSV(PrintWriter writer, Map<String, List<String>> dataMap) throws IOException {
        ArrayList<String> header = new ArrayList<String>();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL);
        try {
            String wi = "";
            for (Iterator<String> iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
                wi = (String) iterator.next();
                header.add(wi);
            }

            csvPrinter.printRecord(header);

            int size = dataMap.get(wi).size();

            for (int i = 0; i < size; i++) {
                List<String> data = new ArrayList<>();
                for (Iterator<String> iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
                    wi = (String) iterator.next();
                    data.add(dataMap.get(wi).get(i));

                }

                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (Exception e) {
            csvPrinter.flush();
            csvPrinter.close();

        }
    }
}
