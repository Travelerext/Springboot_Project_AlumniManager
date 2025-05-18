package jmu.lwk.alumnimanager.controller

import jmu.lwk.alumnimanager.model.Donation
import jmu.lwk.alumnimanager.model.Role
import jmu.lwk.alumnimanager.repository.AlumniRepository
import jmu.lwk.alumnimanager.repository.DonationRepository
import jmu.lwk.alumnimanager.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("donation")
class DonationController(
    val alumniRepository: AlumniRepository,
    val userRepository: UserRepository,
    val donationRepository: DonationRepository
) {

    data class ItemDonationRequest(
        val items: String,
        val donationDate: Instant,
        val message: String
    )

    data class AmountDonationRequest(
        val amount: Long,
        val donationDate: Instant,
        val message: String
    )

    data class DonationResponse(
        val id: String,
        val amount: Long? = null,
        val items: String?= null,
        val donationDate: Instant,
        val message: String,
        val isChecked: Boolean
    )

    @GetMapping("/items")
    fun getItemsDonation() :List<DonationResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        if (user.alumniId == null) throw IllegalArgumentException("未完善信息")
        val alumni = alumniRepository.findById(user.alumniId).orElseThrow { IllegalArgumentException("校友不存在") }
        return donationRepository.findByAlumniIdAndItemsIsNotNull(alumni.id).map { it.toResponse() }
    }

    @GetMapping("/amount")
    fun getAmountDonation() :List<DonationResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        if (user.alumniId == null) throw IllegalArgumentException("未完善信息")
        val alumni = alumniRepository.findById(user.alumniId).orElseThrow { IllegalArgumentException("校友不存在") }
        return donationRepository.findByAlumniIdAndAmountIsNotNull(alumni.id).map { it.toResponse() }
    }

    @GetMapping("/unchecked")
    fun getUncheckedDonation() :List<DonationResponse> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        if (user.role == Role.GeneralAdmin) {
            return donationRepository.findByIsCheckedFalse().map { it.toResponse() }
        } else throw IllegalArgumentException("无权审核")
    }

    @PostMapping("/items")
    fun addItemsDonation(@RequestBody itemDonationRequest: ItemDonationRequest) :DonationResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        if (user.alumniId == null) throw IllegalArgumentException("未完善信息")
        val alumni = alumniRepository.findById(user.alumniId).orElseThrow { IllegalArgumentException("校友不存在") }
        return donationRepository.save(
            Donation(
                items = itemDonationRequest.items,
                donationDate = itemDonationRequest.donationDate,
                alumniId = alumni.id,
                message = itemDonationRequest.message
            )
        ).toResponse()
    }

    @PostMapping("/amount")
    fun addItemsDonation(@RequestBody amountDonationRequest: AmountDonationRequest) :DonationResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        if (user.alumniId == null) throw IllegalArgumentException("未完善信息")
        val alumni = alumniRepository.findById(user.alumniId).orElseThrow { IllegalArgumentException("校友不存在") }
        return donationRepository.save(
            Donation(
                amount = amountDonationRequest.amount,
                donationDate = amountDonationRequest.donationDate,
                alumniId = alumni.id,
                message = amountDonationRequest.message
            )
        ).toResponse()
    }

    @PutMapping("/amount/{id}")
    fun updateAmountDonation(
        @PathVariable id: String,
        @RequestBody amountDonationRequest: AmountDonationRequest
    ): DonationResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId))
            .orElseThrow { IllegalStateException("账号异常") }
        val donation = donationRepository.findById(ObjectId(id))
            .orElseThrow { IllegalArgumentException("捐赠不存在") }
        if (donation.amount == null) {
            throw IllegalArgumentException("该捐赠记录不是金额型捐赠，不可使用金额更新方法")
        }
        if (user.role != Role.GeneralAdmin && donation.alumniId != user.alumniId) {
            throw IllegalArgumentException("无权修改")
        }
        if (donation.isChecked) {
            throw IllegalArgumentException("已审核捐赠不可修改")
        }

        val updatedDonation = donation.copy(
            amount = amountDonationRequest.amount,
            donationDate = amountDonationRequest.donationDate,
            message = amountDonationRequest.message
        )
        return donationRepository.save(updatedDonation).toResponse()
    }

    @PutMapping("/items/{id}")
    fun updateItemDonation(
        @PathVariable id: String,
        @RequestBody itemDonationRequest: ItemDonationRequest
    ): DonationResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId))
            .orElseThrow { IllegalStateException("账号异常") }
        val donation = donationRepository.findById(ObjectId(id))
            .orElseThrow { IllegalArgumentException("捐赠不存在") }
        if (donation.items == null) {
            throw IllegalArgumentException("该捐赠记录不是物品型捐赠，不可使用物品更新方法")
        }
        if (user.role != Role.GeneralAdmin && donation.alumniId != user.alumniId) {
            throw IllegalArgumentException("无权修改")
        }
        if (donation.isChecked) {
            throw IllegalArgumentException("已审核捐赠不可修改")
        }

        val updatedDonation = donation.copy(
            items = itemDonationRequest.items,
            donationDate = itemDonationRequest.donationDate,
            message = itemDonationRequest.message
            // amount 字段保持原值
        )
        return donationRepository.save(updatedDonation).toResponse()
    }



    @PostMapping("/{id}")
    fun checkDonation(@RequestParam approved: Boolean, @PathVariable id: String) :DonationResponse? {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        val user = userRepository.findById(ObjectId(userId)).orElseThrow { IllegalStateException("账号异常") }
        val donation = donationRepository.findById(ObjectId(id)).orElseThrow{ IllegalArgumentException("捐赠不存在") }
        if (user.role == Role.GeneralAdmin) {
            if (approved) {
               return donationRepository.save(donation.copy(isChecked = true)).toResponse()
            }
            else {
                donationRepository.delete(donation)
                return null
            }
        } else throw IllegalArgumentException("无权审核")
    }

    private fun Donation.toResponse(): DonationResponse {
        return DonationResponse(
            id = id.toHexString(),
            amount = amount,
            items = items,
            donationDate = donationDate,
            message = message,
            isChecked = isChecked
        )
    }
}