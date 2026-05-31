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

// function to change font size
def ui.changeFontSize(delta)
    js(delta)
        const root = document.documentElement;
        const current = getComputedStyle(root).getPropertyValue('--font-size').trim();
        const value = Number.parseFloat(current || '22px');
        const next = Math.max(10, Math.min(40, value + delta));

        root.style.setProperty('--font-size', `${next}px`);
    end
end

// function to fetch local time and update the element
def datetime.fetchAndInsert(element)
    js(element)
        const date = new Date();
        const formated = new Intl.DateTimeFormat(navigator.language, {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).format(date);

        element.innerHTML = formated;
    end
end