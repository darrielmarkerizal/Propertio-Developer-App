package com.propertio.developer.dialog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.databinding.ItemCountryPhoneCodeBinding
import com.propertio.developer.dialog.model.PhoneCode


typealias onClickCardListener = (PhoneCode) -> Unit
class PhoneCodeAdapter(
    private val onClickCardListener: onClickCardListener
) : RecyclerView.Adapter<PhoneCodeAdapter.PhoneCodeViewHolder>()
{
    inner class PhoneCodeViewHolder(
        private val binding : ItemCountryPhoneCodeBinding
    ) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(phoneCode: PhoneCode) {
            Log.d("PhoneCodeAdapter", "bind: ${phoneCode.toString()}")
            with(binding) {
                textViewEmojiCountryPhoneCode.text = phoneCode.emoji
                textViewCountryName.text = phoneCode.countryName
                textViewCountryPhoneCode.text = phoneCode.code

                cardViewCountryPhoneCode.setOnClickListener {
                    onClickCardListener(phoneCode)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneCodeViewHolder {
        val binding = ItemCountryPhoneCodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhoneCodeViewHolder(binding)
    }

    override fun getItemCount(): Int = phoneCodeList.size

    override fun onBindViewHolder(holder: PhoneCodeViewHolder, position: Int) {
        holder.bind(phoneCodeList[position])
    }

    private val phoneCodeList : List<PhoneCode> = listOf(
        PhoneCode("ID", "Indonesia", "+62", "🇮🇩"),
        PhoneCode("US", "United States", "+1", "🇺🇸"),
        PhoneCode("CA", "Canada", "+1", "🇨🇦"),
        PhoneCode("GB", "United Kingdom", "+44", "🇬🇧"),
        PhoneCode("AU", "Australia", "+61", "🇦🇺"),
        PhoneCode("NZ", "New Zealand", "+64", "🇳🇿"),
        PhoneCode("MY", "Malaysia", "+60", "🇲🇾"),
        PhoneCode("SG", "Singapore", "+65", "🇸🇬"),
        PhoneCode("HK", "Hong Kong", "+852", "🇭🇰"),
        PhoneCode("MO", "Macau", "+853", "🇲🇴"),
        PhoneCode("CN", "China", "+86", "🇨🇳"),
        PhoneCode("JP", "Japan", "+81", "🇯🇵"),
        PhoneCode("KR", "South Korea", "+82", "🇰🇷"),
        PhoneCode("TW", "Taiwan", "+886", "🇹🇼"),
        PhoneCode("TH", "Thailand", "+66", "🇹🇭"),
        PhoneCode("VN", "Vietnam", "+84", "🇻🇳"),
        PhoneCode("PH", "Philippines", "+63", "🇵🇭"),
        PhoneCode("IN", "India", "+91", "🇮🇳"),
        PhoneCode("PK", "Pakistan", "+92", "🇵🇰"),
        PhoneCode("BD", "Bangladesh", "+880", "🇧🇩"),
        PhoneCode("LK", "Sri Lanka", "+94", "🇱🇰"),
        PhoneCode("MM", "Myanmar", "+95", "🇲🇲"),
        PhoneCode("NP", "Nepal", "+977", "🇳🇵"),
        PhoneCode("IR", "Iran", "+98", "🇮🇷"),
        PhoneCode("IQ", "Iraq", "+964", "🇮🇶"),
        PhoneCode("SA", "Saudi Arabia", "+966", "🇸🇦"),
        PhoneCode("AE", "United Arab Emirates", "+971", "🇦🇪"),
        PhoneCode("AF", "Afghanistan", "+93", "🇦🇫"),
        PhoneCode("DZ", "Algeria", "+213", "🇩🇿"),
        PhoneCode("AS", "American Samoa", "+1", "🇦🇸"),
        PhoneCode("AD", "Andorra", "+376", "🇦🇩"),
        PhoneCode("AO", "Angola", "+244", "🇦🇴"),
        PhoneCode("AI", "Anguilla", "+1", "🇦🇮"),
        PhoneCode("AG", "Antigua and Barbuda", "+1", "🇦🇬"),
        PhoneCode("AR", "Argentina", "+54", "🇦🇷"),
        PhoneCode("AM", "Armenia", "+374", "🇦🇲"),
        PhoneCode("AW", "Aruba", "+297", "🇦🇼"),
        PhoneCode("AT", "Austria", "+43", "🇦🇹"),
        PhoneCode("AZ", "Azerbaijan", "+994", "🇦🇿"),
        PhoneCode("BS", "Bahamas", "+1", "🇧🇸"),
        PhoneCode("BH", "Bahrain", "+973", "🇧🇭"),
        PhoneCode("BB", "Barbados", "+1", "🇧🇧"),
        PhoneCode("BY", "Belarus", "+375", "🇧🇾"),
        PhoneCode("BE", "Belgium", "+32", "🇧🇪"),
        PhoneCode("BZ", "Belize", "+501", "🇧🇿"),
        PhoneCode("BJ", "Benin", "+229", "🇧🇯"),
        PhoneCode("BM", "Bermuda", "+1", "🇧🇲"),
        PhoneCode("BT", "Bhutan", "+975", "🇧🇹"),
        PhoneCode("BO", "Bolivia", "+591", "🇧🇴"),
        PhoneCode("BA", "Bosnia and Herzegovina", "+387", "🇧🇦"),
        PhoneCode("BW", "Botswana", "+267", "🇧🇼"),
        PhoneCode("BR", "Brazil", "+55", "🇧🇷"),
        PhoneCode("IO", "British Indian Ocean Territory", "+246", "🇮🇴"),
        PhoneCode("VG", "British Virgin Islands", "+1", "🇻🇬"),
        PhoneCode("BN", "Brunei", "+673", "🇧🇳"),
        PhoneCode("BG", "Bulgaria", "+359", "🇧🇬"),
        PhoneCode("BF", "Burkina Faso", "+226", "🇧🇫"),
        PhoneCode("BI", "Burundi", "+257", "🇧🇮"),
        PhoneCode("KH", "Cambodia", "+855", "🇰🇭"),
        PhoneCode("CM", "Cameroon", "+237", "🇨🇲"),
        PhoneCode("CV", "Cape Verde", "+238", "🇨🇻"),
        PhoneCode("KY", "Cayman Islands", "+1", "🇰🇾"),
        PhoneCode("CF", "Central African Republic", "+236", "🇨🇫"),
        PhoneCode("TD", "Chad", "+235", "🇹🇩"),
        PhoneCode("CL", "Chile", "+56", "🇨🇱"),
        PhoneCode("CO", "Colombia", "+57", "🇨🇴"),

        )

}
