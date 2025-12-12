package com.example.Indrugs.services;

import com.example.Indrugs.DTO.MedicamentoDTO;
import com.example.Indrugs.entities.Medicamentos;
import com.example.Indrugs.mapper.MedicamentosMap;
import com.example.Indrugs.repositorios.MedicamentoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class MedicamentosServiceImpl implements MedicamentosService{

    private final ImgBBService imgBBService;
    private final MedicamentosMap medicamentosMap;
    MedicamentoRepository medicRepository;

    public MedicamentosServiceImpl(MedicamentoRepository medicRepository, ImgBBService imgBBService, MedicamentosMap medicamentosMap){
        this.medicRepository = medicRepository;
        this.imgBBService = imgBBService;
        this.medicamentosMap = medicamentosMap;
    }

    @Override
    public List<MedicamentoDTO> readAdmin() {
        List<Medicamentos> medic = medicRepository.findAll();
        return medic.stream()
                .map(MedicamentosMap ::mapToDtoAdmin)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MedicamentoDTO> readAd(Pageable pageable) {
        Page<Medicamentos> medic = medicRepository.findAll(pageable);
        return medic.map(MedicamentosMap ::mapToDtoAdmin);
    }

    @Override
    public Page<MedicamentoDTO> mostarEnPaciente(Pageable pageable) {
        Page<Medicamentos> medic = medicRepository.findAll(pageable);
        return medic.map(medicamentosMap::mapToPaciente);
    }

    @Override
    @Transactional
    public void crear(MedicamentoDTO MedDto) {
        if(medicRepository.existsByNombreMedicamento(MedDto.getNombreMedicamento())){
            throw new RuntimeException("Ya existe este medicamento");
        }
        Medicamentos medic = MedicamentosMap.mapToEntitie(MedDto);
        medicRepository.save(medic);
    }

    @Override
    public void actualizar(Long idMedicamento, MedicamentoDTO MedDto) {
        Medicamentos medic = medicRepository.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
        MedicamentosMap.mapUpdate(medic, MedDto);
        medicRepository.save(medic);
    }

    @Override
    public List<MedicamentoDTO> findByNombre(String nombreMedicamento) {
        List<Medicamentos> medic = medicRepository.findByNombreMedicamentoContainingIgnoreCase(nombreMedicamento);
        return medic.stream()
                .map(medicamentosMap ::mapToPaciente)
                .collect(Collectors.toList());
    }

    @Override
    public MedicamentoDTO buscarPorIdMedicamento(Long idMedicamento) {
        Medicamentos medicamento = medicRepository.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
        return medicamentosMap.mapToPaciente(medicamento);

    }
}
