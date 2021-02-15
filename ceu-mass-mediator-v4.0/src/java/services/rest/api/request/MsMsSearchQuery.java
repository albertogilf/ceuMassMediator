package services.rest.api.request;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class MsMsSearchQuery {
    private double ion_mass;
    private List<Spectrum> ms_ms_peaks;
    private double precursor_ion_tolerance;
    private ToleranceMode precursor_ion_tolerance_mode;
    private double precursor_mz_tolerance;
    private ToleranceMode precursor_mz_tolerance_mode;
    private IonModeMS ion_mode;
    private IonizationVoltage ionization_voltage;
    private List<SpectraType> spectra_types;

    public MsMsSearchQuery(double ion_mass, 
            double precursor_ion_tolerance, ToleranceMode precursor_ion_tolerance_mode,
            double precursor_mz_tolerance, ToleranceMode precursor_mz_tolerance_mode,
            IonModeMS ion_mode, IonizationVoltage ionizationVoltage) {
        this.ion_mass = ion_mass;
        this.precursor_ion_tolerance = precursor_ion_tolerance;
        this.precursor_ion_tolerance_mode = precursor_ion_tolerance_mode;
        this.precursor_mz_tolerance = precursor_mz_tolerance;
        this.precursor_mz_tolerance_mode = precursor_mz_tolerance_mode;
        this.ion_mode = ion_mode;
        this.ionization_voltage = ionizationVoltage;
        this.ms_ms_peaks = new ArrayList<Spectrum>();
        this.spectra_types = new ArrayList<>();
    }

    public MsMsSearchQuery(double ion_mass, 
            List<Spectrum> ms_ms_peaks,
            double precursor_ion_tolerance, ToleranceMode precursor_ion_tolerance_mode,
            double precursor_mz_tolerance, ToleranceMode precursor_mz_tolerance_mode,
            IonModeMS ion_mode, IonizationVoltage ionizationVoltage,
            List<SpectraType> spectra_types) {
        this(ion_mass, 
            precursor_ion_tolerance, precursor_ion_tolerance_mode,
            precursor_mz_tolerance, precursor_mz_tolerance_mode,
            ion_mode, ionizationVoltage);
        this.ms_ms_peaks = ms_ms_peaks;
        this.spectra_types = spectra_types;
    }

    public double getIon_mass() {
        return ion_mass;
    }

    public List<Spectrum> getMs_ms_peaks() {
        return ms_ms_peaks;
    }

    public double getPrecursor_ion_tolerance() {
        return precursor_ion_tolerance;
    }

    public ToleranceMode getPrecursor_ion_tolerance_mode() {
        return precursor_ion_tolerance_mode;
    }

    public double getPrecursor_mz_tolerance() {
        return precursor_mz_tolerance;
    }

    public ToleranceMode getPrecursor_mz_tolerance_mode() {
        return precursor_mz_tolerance_mode;
    }

    public IonModeMS getIon_mode() {
        return ion_mode;
    }

    public IonizationVoltage getIonization_voltage() {
        return ionization_voltage;
    }

    public List<SpectraType> getSpectra_types() {
        return spectra_types;
    }

    public void addMs_ms_peak(Spectrum spectrum) {
        this.ms_ms_peaks.add(spectrum);
    }

    public void addSpectra_types(SpectraType type) {
        this.spectra_types.add(type);
    }

    public static void main(String[] args) {
        MsMsSearchQuery demoData
                            = new MsMsSearchQuery(
                        147.0,
                        100,
                        ToleranceMode.MDA,
                        500,
                        ToleranceMode.MDA,
                        IonModeMS.POSITIVE,
                        IonizationVoltage.LOW);
        demoData.addMs_ms_peak(new Spectrum(40.948, 0.174));
        demoData.addMs_ms_peak(new Spectrum(56.022, 0.424));
        demoData.addMs_ms_peak(new Spectrum(84.37, 53.488));
        demoData.addMs_ms_peak(new Spectrum(101.50, 8.285));
        demoData.addMs_ms_peak(new Spectrum(102.401, 0.775));
        demoData.addMs_ms_peak(new Spectrum(129.670, 100.000));
        demoData.addMs_ms_peak(new Spectrum(146.966, 20.070));

        demoData.addSpectra_types(SpectraType.EXPERIMENTAL);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println(result);

        MsMsSearchQuery received = gson.fromJson(result, MsMsSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    } 
}
