// function to focus on an element by its id
def navigation.focusById(id)
    js(id)
        document.getElementById(id).focus()
    end
end

// function to toggle between light and dark theme
def theme.toggle(switch)
    if switch.checked
        remove @data-webtui-theme from document.documentElement
    else
        set document.documentElement@data-webtui-theme to "dark"
    end
end