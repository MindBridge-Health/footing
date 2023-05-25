package com.palaver.service.mapper

import com.palaver.data.generated.BenefactorData
import com.palaver.service.model.Benefactor
import org.mapstruct.Mapper

@Mapper
interface BenefactorDataMapper {

    fun benefactorDataToBenefactor(benefactorData: BenefactorData) : Benefactor

    fun benefactorToBenefactorData(benefactor: Benefactor) : BenefactorData
}